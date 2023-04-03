package pl.pacinho.thousand.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.pacinho.thousand.config.UIConfig;
import pl.pacinho.thousand.model.dto.CardDto;
import pl.pacinho.thousand.model.dto.GameDto;
import pl.pacinho.thousand.model.dto.GiveCardRequestDto;
import pl.pacinho.thousand.model.dto.RoundResultDto;
import pl.pacinho.thousand.model.enums.GameStage;
import pl.pacinho.thousand.model.enums.GameStatus;
import pl.pacinho.thousand.service.GameService;

@RequiredArgsConstructor
@Controller
public class GameController {

    private final GameService gameService;

    @GetMapping(UIConfig.PREFIX)
    public String gameHome(Model model) {
        return "home";
    }

    @PostMapping(UIConfig.GAMES)
    public String availableGames(Model model) {
        model.addAttribute("games", gameService.getAvailableGames());
        return "fragments/available-games :: availableGamesFrag";
    }

    @PostMapping(UIConfig.NEW_GAME)
    public String newGame(Model model, Authentication authentication) {
        try {
            return "redirect:" + UIConfig.GAMES + "/" + gameService.newGame(authentication.getName(), 3) + "/room";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return gameHome(model);
        }
    }

    @GetMapping(UIConfig.GAME_ROOM)
    public String gameRoom(@PathVariable(value = "gameId") String gameId, Model model, Authentication authentication) {
        try {
            GameDto game = gameService.findDtoById(gameId, authentication.getName());
            if (game.getStage() == GameStage.ROUND_OVER)
                return "redirect:" + UIConfig.GAMES + "/" + gameId + "/round/summary";
            if (game.getStatus() == GameStatus.IN_PROGRESS)
                return "redirect:" + UIConfig.GAMES + "/" + gameId;
            if (game.getStatus() == GameStatus.FINISHED)
                throw new IllegalStateException("Game " + gameId + " finished!");

            model.addAttribute("game", game);
            model.addAttribute("joinGame", gameService.canJoin(game, authentication.getName()));
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return gameHome(model);
        }
        return "game-room";
    }

    @PostMapping(UIConfig.PLAYERS)
    public String players(@PathVariable(value = "gameId") String gameId,
                          Model model,
                          Authentication authentication) {
        GameDto game = gameService.findDtoById(gameId, authentication.getName());
        model.addAttribute("game", game);
        return "fragments/game-players :: gamePlayersFrag";
    }

    @GetMapping(UIConfig.GAME_PAGE)
    public String gamePage(@PathVariable(value = "gameId") String gameId,
                           Model model,
                           RedirectAttributes redirectAttr,
                           Authentication authentication) {

        GameDto game = gameService.findDtoById(gameId, authentication.getName());

        if (game.getStatus() == GameStatus.FINISHED) {
            redirectAttr.addAttribute("gameId", gameId);
            return "redirect:" + UIConfig.GAME_OVER;
        }

        model.addAttribute("game", game);
        return "game";
    }


    @GetMapping(UIConfig.GAME_BOARD_RELOAD)
    public String reloadBoard(Authentication authentication,
                              Model model,
                              @PathVariable(value = "gameId") String gameId) {
        model.addAttribute("game", gameService.findDtoById(gameId, authentication.getName()));
        return "fragments/board :: boardFrag";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(UIConfig.GAME_AUCTION_OFFER)
    public void auctionOffer(Authentication authentication,
                             @RequestParam("value") int value,
                             @PathVariable(value = "gameId") String gameId) {
        gameService.checkOffer(authentication.getName(), value, gameId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(UIConfig.GAME_AUCTION_PASS)
    public void auctionPass(Authentication authentication,
                            @PathVariable(value = "gameId") String gameId) {
        gameService.auctionPass(authentication.getName(), gameId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(UIConfig.GAME_AUCTION_GIVE_CARD)
    public void auctionGiveCard(Authentication authentication,
                                @RequestBody GiveCardRequestDto giveCardRequest,
                                @PathVariable(value = "gameId") String gameId) {
        gameService.auctionGiveCard(authentication.getName(), gameId, giveCardRequest);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(UIConfig.GAME_AUCTION_CONFIRMATION_POINTS)
    public void auctionGiveCard(Authentication authentication,
                                @RequestParam("value") int value,
                                @PathVariable(value = "gameId") String gameId) {
        gameService.auctionConfirmationPoints(authentication.getName(), gameId, value);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(UIConfig.GAME_MOVE)
    public void move(Authentication authentication,
                     @RequestBody CardDto cardDto,
                     @PathVariable(value = "gameId") String gameId) {
        gameService.move(authentication.getName(), gameId, cardDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(UIConfig.GAME_BOMB)
    public void bomb(Authentication authentication,
                     @PathVariable(value = "gameId") String gameId) {
        gameService.bomb(authentication.getName(), gameId);
    }

    @GetMapping(UIConfig.GAME_ROUND_SUMMARY)
    public String roundSummary(Model model,
                               Authentication authentication,
                               @PathVariable(value = "gameId") String gameId) {
        RoundResultDto roundResult = gameService.getRoundResult(gameId);
        if (roundResult == null)
            return "redirect:" + UIConfig.GAMES + "/" + gameId;

        model.addAttribute("gameId", gameId);
        model.addAttribute("roundResult", roundResult);
        model.addAttribute("readyBtn", !gameService.isReady(roundResult, authentication.getName()));
        return "round-result";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(UIConfig.GAME_ROUND_READY)
    public void roundReady(@PathVariable(value = "gameId") String gameId, Authentication authentication) {
        gameService.playerReady(gameId, authentication.getName());
    }

    @GetMapping(UIConfig.GAME_ROUND_SUMMARY_RELOAD)
    public String reloadRoundResult(Model model,
                                    Authentication authentication,
                                    @PathVariable(value = "gameId") String gameId) {
        RoundResultDto roundResult = gameService.getRoundResult(gameId);
        if (roundResult == null)
            return "redirect:" + UIConfig.GAMES + "/" + gameId;

        model.addAttribute("gameId", gameId);
        model.addAttribute("roundResult", roundResult);
        model.addAttribute("readyBtn", !gameService.isReady(roundResult, authentication.getName()));
        return "fragments/round-result-table :: roundResultTable";
    }


}