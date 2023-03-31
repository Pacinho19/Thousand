package pl.pacinho.thousand.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.pacinho.thousand.model.dto.AuctionOfferDto;
import pl.pacinho.thousand.model.dto.CardDto;
import pl.pacinho.thousand.model.dto.GameDto;
import pl.pacinho.thousand.model.dto.GiveCardRequestDto;
import pl.pacinho.thousand.model.dto.mapper.GameDtoMapper;
import pl.pacinho.thousand.model.entity.Game;
import pl.pacinho.thousand.model.entity.Player;
import pl.pacinho.thousand.model.enums.GameStage;
import pl.pacinho.thousand.model.enums.GameStatus;
import pl.pacinho.thousand.repository.GameRepository;
import pl.pacinho.thousand.utils.AuctionUtils;
import pl.pacinho.thousand.utils.GameUtils;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final GameLogicService gameLogicService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public List<GameDto> getAvailableGames() {
        return gameRepository.getAvailableGames();
    }

    public String newGame(String name, int playersCount) {
        List<GameDto> activeGames = getAvailableGames();
        if (activeGames.size() >= 10)
            throw new IllegalStateException("Cannot create new Game! Active game count : " + activeGames.size());

        String gameId = gameRepository.newGame(name, playersCount);
        simpMessagingTemplate.convertAndSend("/game-created", true);
        return gameId;
    }

    public GameDto findDtoById(String gameId, String name) {
        return GameDtoMapper.parse(gameLogicService.findById(gameId), name);
    }

    public void joinGame(String name, String gameId) throws IllegalStateException {
        Game game = gameRepository.joinGame(name, gameId);
        if (game.getPlayers().size() == game.getPlayersCount()) game.setStatus(GameStatus.IN_PROGRESS);
    }

    public boolean checkStartGame(String gameId) {
        Game game = gameLogicService.findById(gameId);
        boolean startGame = game.getPlayers().size() == game.getPlayersCount();

        if (startGame) gameLogicService.dealTheCards(game);
        return startGame;
    }

    public boolean canJoin(GameDto game, String name) {
        return game.getPlayers().size() < game.getPlayersCount() && game.getPlayers().stream().noneMatch(p -> p.equals(name));
    }

    public void checkOffer(String name, int value, String gameId) {
        Game game = gameLogicService.findById(gameId);
        if (!validateOffer(name, value, game, false)) return;

        game.getAuctionDto().addOffer(name, new AuctionOfferDto(value, false));

        gameLogicService.setNextAuctionPlayer(game);
        simpMessagingTemplate.convertAndSend("/reload-board/" + game.getId(), true);
    }

    public void auctionPass(String name, String gameId) {
        Game game = gameLogicService.findById(gameId);
        if (!validateOffer(name, 0, game, true)) return;

        game.getAuctionDto().addOffer(name, new AuctionOfferDto(0, true));
        if (!AuctionUtils.checkEndAuction(game)) gameLogicService.setNextAuctionPlayer(game);
        else gameLogicService.endAuction(game);

        simpMessagingTemplate.convertAndSend("/reload-board/" + game.getId(), true);
    }

    private boolean validateOffer(String name, int value, Game game, boolean pass) {
        if (game.getAuctionDto() == null) return false;  //TODO MESSAGE

        if (!pass && value <= game.getAuctionDto().getHighestOffer()) return false; //TODO MESSAGE

        if (!pass && value > AuctionUtils.getPlayerCardsValue(game, name)) return false; //TODO MESSAGE

        if (!pass && value % 10 > 0) return false; //TODO MESSAGE

        if (!game.getPlayers().get(game.getActualPlayer() - 1).getName().equals(name)) return false; //TODO MESSAGE

        return true;
    }

    @SneakyThrows
    public void auctionGiveCard(String name, String gameId, GiveCardRequestDto giveCardRequest) {
        Game game = gameLogicService.findById(gameId);
        if (game.getAuctionSummary() == null || !game.getAuctionSummary().playerName().equals(name))
            return; //TODO MESSAGE

        if (giveCardRequest == null) return; //TODO MESSAGE

        Player targetPlayer = GameUtils.getPlayer(game, giveCardRequest.playerName());
        targetPlayer.addCards(List.of(giveCardRequest.card()));

        Player sourcePlayer = GameUtils.getPlayer(game, name);
        sourcePlayer.getCards().remove(GameUtils.findCard(sourcePlayer.getCards(), giveCardRequest.card()));

        if (GameUtils.allPlayersHasTheSameCardsCount(game)) game.setStage(GameStage.CONFIRMATION_POINTS);

        simpMessagingTemplate.convertAndSend("/reload-board/" + game.getId(), true);
        Thread.sleep(500);
        simpMessagingTemplate.convertAndSendToUser(giveCardRequest.playerName(), "/reload-board/" + game.getId(), giveCardRequest.card());
    }

    public void auctionConfirmationPoints(String name, String gameId, int value) {
        Game game = gameLogicService.findById(gameId);
        if (game.getAuctionSummary() == null || !game.getAuctionSummary().playerName().equals(name))
            return; //TODO MESSAGE

        if (game.getAuctionSummary().value() > value) return; //TODO MESSAGE

        game.setRoundPoints(value);
        game.setStage(GameStage.GAME_ON);

        simpMessagingTemplate.convertAndSend("/reload-board/" + game.getId(), true);
    }

    public void move(String name, String gameId, CardDto cardDto) {
        Game game = gameLogicService.findById(gameId);

        if (!GameUtils.checkPlayer(game, name))
            return; //TODO MESSAGE

        Player player = GameUtils.getPlayer(game, name);

        if (!GameUtils.checkCard(player.getCards(), cardDto))
            return; //TODO MESSAGE

        game.addCardToStack(player, cardDto);
        player.getCards().remove(GameUtils.findCard(player.getCards(), cardDto));

        if (game.getStack().size() == 3)
            gameLogicService.checkBattle(game);
        else
            game.setActualPlayer(game.getNextPlayer(1));

        simpMessagingTemplate.convertAndSend("/reload-board/" + game.getId(), true);
    }

    private void checkBattle(Game game) {

        game.clearStack();
    }
}