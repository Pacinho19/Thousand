package pl.pacinho.thousand.config;

public class UIConfig {
    public static final String PREFIX = "/thousand";
    public static final String GAMES = PREFIX + "/games";
    public static final String NEW_GAME = GAMES + "/new";
    public static final String GAME_PAGE = GAMES + "/{gameId}";
    public static final String GAME_ROOM = GAME_PAGE + "/room";
    public static final String PLAYERS = GAME_ROOM + "/players";
    public static final String GAME_BOARD = GAME_PAGE + "/board";
    public static final String GAME_BOARD_RELOAD = GAME_BOARD + "/reload";
    public static final String GAME_OVER = GAME_PAGE + "/over";
    public static final String GAME_AUCTION = GAME_PAGE + "/auction";
    public static final String GAME_AUCTION_OFFER = GAME_AUCTION + "/offer";
    public static final String GAME_AUCTION_PASS = GAME_AUCTION + "/pass";
    public static final String GAME_AUCTION_GIVE_CARD = GAME_AUCTION + "/give-card";
    public static final String GAME_AUCTION_CONFIRMATION_POINTS = GAME_AUCTION + "/confirmation-points";
}