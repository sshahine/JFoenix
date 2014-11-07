package com.aquafx_project.nativestuff;

public enum NsImageIcon {
    QUICK_LOOK_TEMPLATE("NSQuickLookTemplate"),
    BLUETOOTH_TEMPLATE("NSBluetoothTemplate"),
    I_CHAT_THEATER_TEMPLATE("NSIChatTheaterTemplate"),
    SLIDESHOW_TEMPLATE("NSSlideshowTemplate"),
    ACTION_TEMPLATE("NSActionTemplate"),
    SMART_BADGE_TEMPLATE("NSSmartBadgeTemplate"),
    SHARE_TEMPLATE("NSShareTemplate"),
    PATH_TEMPLATE("NSPathTemplate"),
    INVALID_DATA_FREESTANDING_TEMPLATE("NSInvalidDataFreestandingTemplate"),
    LOCK_LOCKED_TEMPLATE("NSLockLockedTemplate"),
    LOCK_UNLOCKED_TEMPLATE("NSLockUnlockedTemplate"),
    GO_RIGHT_TEMPLATE("NSGoRightTemplate"),
    GO_LEFT_TEMPLATE("NSGoLeftTemplate"),
    RIGHT_FACING_TRIANGLE_TEMPLATE("NSRightFacingTriangleTemplate"),
    LEFT_FACING_TRIANGLE_TEMPLATE("NSLeftFacingTriangleTemplate"),
    ADD_TEMPLATE("NSAddTemplate"),
    REMOVE_TEMPLATE("NSRemoveTemplate"),
    REVEAL_FREESTANDING_TEMPLATE("NSRevealFreestandingTemplate"),
    FOLLOW_LINK_FREESTANDING_TEMPLATE("NSFollowLinkFreestandingTemplate"),
    ENTER_FULL_SCREEN_TEMPLATE("NSEnterFullScreenTemplate"),
    EXIT_FULL_SCREEN_TEMPLATE("NSExitFullScreenTemplate"),
    STOP_PROGRESS_TEMPLATE("NSStopProgressTemplate"),
    STOP_PRPGRESS_FREESTANDING_TEMPLATE("NSStopProgressFreestandingTemplate"),
    REFRESH_TEMPLATE("NSRefreshTemplate"),
    REFRESH_FREESTANDING_TEMPLATE("NSRefreshFreestandingTemplate"),
    FOLDER("NSFolder"),
    TRASH_EMPTY("NSTrashEmpty"),
    TRASH_FULL("NSTrashFull"),
    HOME_TEMPLATE("NSHomeTemplate"),
    BOOKMARKS_TEMPLATE("NSBookmarksTemplate"),
    CAUTION("NSCaution"),
    STATUS_AVAILABLE("NSStatusAvailable"),
    STATUS_PARTIALLY_AVAILABLE("NSStatusPartiallyAvailable"),
    STATUS_UNAVAILABLE("NSStatusUnavailable"),
    STATUS_NONE("NSStatusNone"),
    APPLICATION_ICON("NSApplicationIcon"),
    MENU_ON_STATE_TEMPLATE("NSMenuOnStateTemplate"),
    MENU_MIXED_STATE_TEMPLATE("NSMenuMixedStateTemplate"),
    USER_GUEST("NSUserGuest"),
    MOBILE_ME("NSMobileMe"),
    MULTIPLE_DOCUMENTS("NSMultipleDocuments"),
    USER("NSUser"),
    USER_GROUP("NSUserGroup"),
    EVERYONE("NSEveryone"),
    BONJOUR("NSBonjour"),
    DOT_MAC("NSDotMac"),
    COMPUTER("NSComputer"),
    FOLDER_BURNABLE("NSFolderBurnable"),
    FOLDER_SMART("NSFolderSmart"),
    NETWORK("NSNetwork"),
    USER_ACCOUNTS("NSUserAccounts"),
    PREFERENCES_GENERAL("NSPreferencesGeneral"),
    ADVANCED("NSAdvanced"),
    INFO("NSInfo"),
    FONT_PANEL("NSFontPanel"),
    COLOR_PANEL("NSColorPanel"),
    ICON_VIEW_TEMPLATE("NSIconViewTemplate"),
    LIST_VIEW_TEMPLATE("NSListViewTemplate"),
    COLUMN_VIEW_TEMPLATE("NSColumnViewTemplate"),
    FLOW_VIEW_TEMPLATE("NSFlowViewTemplate");
    

    // NSImageNameColorPanel ->
    // Toolkit.getDefaultToolkit().getImage("NSImage://NSColorPanel")
    // COLOR_PANEL("NSColorPanel"), QUICKLOOK_TEMPLATE("NSQuickLookTemplate"),
    // FOLDER_BURNABLE("NSFolderBurnable");

    private String name;

    private NsImageIcon(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
