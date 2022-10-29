package michigan.mahjong

fun MainMenuView(context: Context, navController: NavHostController){

    var isLaunching by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isLaunching) {
            isLaunching = false
            getChatts(context)
        }
    }



}