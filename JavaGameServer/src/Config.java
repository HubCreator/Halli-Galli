class UserConfig {
	public static final double P1_DEG = -45.0;
	public static final double P2_DEG = 45.0;
	public static final double P3_DEG = -45.0;
	public static final double P4_DEG = 45.0;

	public static final int P1_DOWNX = 28;
	public static final int P1_DOWNY = 121;
	public static final int P2_DOWNX = 692;
	public static final int P2_DOWNY = 121;
	public static final int P3_DOWNX = 692;
	public static final int P3_DOWNY = 506;
	public static final int P4_DOWNX = 28;
	public static final int P4_DOWNY = 506;

	public static final int P1_UPX = 215;
	public static final int P1_UPY = 206;
	public static final int P2_UPX = 528;
	public static final int P2_UPY = 206;
	public static final int P3_UPX = 528;
	public static final int P3_UPY = 415;
	public static final int P4_UPX = 215;
	public static final int P4_UPY = 415;
}

class BackgroundConfig {
	public static final String WELCOME = "images/welcome.jpg";
	public static final String BACKGROUND_PANEL = "images/background_panel.jpg"; 
	public static final String BACKGROUND = "images/background.jpg";
	
	public static final String BACKGROUND_P1 = "images/background_p1.jpg";
	public static final String BACKGROUND_P2 = "images/background_p2.jpg";
	public static final String BACKGROUND_P3 = "images/background_p3.jpg";
	public static final String BACKGROUND_P4 = "images/background_p4.jpg";
}

class CardConfig {
	public static final int CARD_WIDTH = 180;
	public static final int CARD_HEIGHT = 130;

	public static final String BLANK = "images/blank.png";
	public static final String BACK = "images/back.png";
	
	public static final String BANANA = "banana";
	public static final String BANANA1 = "images/banana1.png";
	public static final String BANANA2 = "images/banana2.png";
	public static final String BANANA3 = "images/banana3.png";
	public static final String BANANA4 = "images/banana4.png";
	public static final String BANANA5 = "images/banana5.png";

	public static final String PEAR = "pear";
	public static final String PEAR1 = "images/pear1.png";
	public static final String PEAR2 = "images/pear2.png";
	public static final String PEAR3 = "images/pear3.png";
	public static final String PEAR4 = "images/pear4.png";
	public static final String PEAR5 = "images/pear5.png";

	public static final String PLUM = "plum";
	public static final String PLUM1 = "images/plum1.png";
	public static final String PLUM2 = "images/plum2.png";
	public static final String PLUM3 = "images/plum3.png";
	public static final String PLUM4 = "images/plum4.png";
	public static final String PLUM5 = "images/plum5.png";

	public static final String BERRY = "berry";
	public static final String BERRY1 = "images/berry1.png";
	public static final String BERRY2 = "images/berry2.png";
	public static final String BERRY3 = "images/berry3.png";
	public static final String BERRY4 = "images/berry4.png";
	public static final String BERRY5 = "images/berry5.png";
}

class CardCntConfig {
	public static final int CNT_W = 66;
	public static final int CNT_H = 27;
	
	
	public static final int P1_X = 115;
	public static final int P1_Y = 235;
	
	public static final int P2_X = 758;
	public static final int P2_Y = 235;
	
	public static final int P3_X = 758;
	public static final int P3_Y = 480;
	
	public static final int P4_X = 115;
	public static final int P4_Y = 480;
}

class BellConfig {
	public static final String BELL = "images/bell.png";
	public static final int BELL_WIDTH = 232;
	public static final int BELL_HEIGHT = 195;
	public static final int BELLX = 336;
	public static final int BELLY = 265;
}

class ButtonsConfig {
	public static final String ENTER_BUTTON_UP = "images/Enter_btnUp.jpg";
	public static final String ENTER_BUTTON_DOWN = "images/Enter_btnDown.jpg";
	
	public static final String START_IMAGE = "images/start-button.png";
	public static final String RESTART_IMAGE = "images/restart.png";
	
	
	public static final int STARTX = 400;
	public static final int STARTY = 115;
}

class RankConfig {
	public static final String GOLD = "images/gold.png";
	public static final String SILVER = "images/silver.png";
	public static final String BRONZE = "images/bronze.png";
	public static final String LOOSER = "images/ghost.png";
}

class ImageLabels {
	public static final String SEND = "images/send1.jpg";
	public static final String SEND_PRESSED = "images/send2.jpg";
	public static final String EXIT = "images/exit.png";
	public static final String EXIT_PRESSED = "images/exit_pressed.png";
	public static final String CREATE_ROOM = "images/room.png";
}
class BombComfig {
	public static final String BOMB = "images/boom.png";
	public static final int BOMB_X = 300;
	public static final int BOMB_Y = 100;
	public static final int BOMB_W = 300;
	public static final int BOMB_H = 260;
}

class Protocol {
	public static final String LOGIN = "100";
	public static final String MSG_WAITING = "200";
	public static final String MSG_ROOM = "201";
	public static final String LOGOUT = "400";
	public static final String CREATE_NEW_ROOM = "600";
	public static final String ROOM_LIST = "601";
	public static final String ENTERING_MASTER = "603";
	public static final String EXIT_PLAYER = "604";
	public static final String ENTER_ROOM = "606";
	public static final String OBSERVE_ROOM = "607";
	public static final String EXIT_OBSERVER = "608";
	public static final String RELOAD = "609";
	
	public static final String GAME_START = "700";
	public static final String GAME_RESTART = "701";
	public static final String CARD_CLICKED = "702";
	public static final String BELL_HIT = "703";
	public static final String PLAYER_DEAD = "704";
	public static final String GAME_OVER = "705";
	
}