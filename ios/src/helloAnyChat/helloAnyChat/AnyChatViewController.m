//
//  AnyChatViewController.m
//  helloAnyChat
//
//  Created by AnyChat on 14-9-12.
//  Copyright (c) 2014年 GuangZhou BaiRui NetWork Technology Co.,Ltd. All rights reserved.
//

#import "AnyChatViewController.h"

// Local Settings Parameter Key Define
NSString* const kUseP2P = @"usep2p";
NSString* const kUseServerParam = @"useserverparam";
NSString* const kVideoSolution = @"videosolution";
NSString* const kVideoFrameRate = @"videoframerate";
NSString* const kVideoBitrate = @"videobitrate";
NSString* const kVideoPreset = @"videopreset";
NSString* const kVideoQuality = @"videoquality";

@interface AnyChatViewController ()

@end

@implementation AnyChatViewController

@synthesize anyChat;
@synthesize videoVC;
@synthesize onLineUserTableView;
@synthesize onlineUserMArray;
@synthesize theOnLineLoginState;
@synthesize theVersion;
@synthesize theStateInfo;
@synthesize theRoomNO;
@synthesize theUserName;
@synthesize theServerIP;
@synthesize theServerPort;
@synthesize theLoginBtn;
@synthesize theLoginAlertView;
@synthesize theHideKeyboardBtn;
@synthesize theMyUserID;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        
    }
    return self;
}


- (void)viewDidLoad
{
    [super viewDidLoad];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(AnyChatNotifyHandler:) name:@"ANYCHATNOTIFY" object:nil];
    
    [AnyChatPlatform InitSDK:0];
    
    anyChat = [[AnyChatPlatform alloc] init];
    anyChat.notifyMsgDelegate = self;
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:YES];
    [self setUIControls];
}

#pragma mark - Memory Warning Method

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - Rotation

- (BOOL)shouldAutorotate
{
    return NO;
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return onlineUserMArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *Cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (Cell == nil)
    {
        NSArray *nibs = [[NSBundle mainBundle] loadNibNamed:@"tableVCell" owner:self options:nil];
        Cell = [nibs objectAtIndex:0];
    }
    
    NSInteger userID = [[onlineUserMArray objectAtIndex:[indexPath row]] intValue];
    NSString *name = [AnyChatPlatform GetUserName:userID];;
    
    UILabel *userIDLabel = (UILabel *)[Cell.contentView viewWithTag:kUserIDValueTag];
    UILabel *nameLabel = (UILabel *)[Cell.contentView viewWithTag:kNameValueTag];
    UIImageView *bgView = (UIImageView *)[Cell viewWithTag:kBackgroundViewTag];
    
    if (theMyUserID == userID)
    {
        nameLabel.text = [name stringByAppendingString:@"(自己)"];
    }
    else
    {
        nameLabel.text = name;
    }
    
    userIDLabel.text = [NSString stringWithFormat:@"%i",userID];
    
    NSString *RandomNo = [[NSString alloc] initWithFormat:@"%i",[self getRandomNumber:1 to:5]];
    bgView.image = [UIImage imageNamed:RandomNo];
    
    Cell.selectionStyle = UITableViewCellSelectionStyleDefault;
    
    return Cell;
}

-(void) tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if([indexPath row] == ((NSIndexPath*)[[tableView indexPathsForVisibleRows] lastObject]).row)
    {   //Display Cell end of loading
        theOnLineLoginState = YES;
    }
    else
    {
        theOnLineLoginState = NO;
    }
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    int selectID = [[onlineUserMArray objectAtIndex:[indexPath row]] integerValue];
    
    if (selectID != theMyUserID) {
        videoVC = [VideoViewController new];
        videoVC.iRemoteUserId = selectID;
        [self.navigationController pushViewController:videoVC animated:YES];
    }
}

- (CGFloat)tableView:(UITableView *)tabelView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
	return 70.0f;
}


#pragma mark - TextField Delegate

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    return YES;
}

#pragma mark - AnyChatNotifyMessageDelegate

// 连接服务器消息
- (void) OnAnyChatConnect:(BOOL) bSuccess
{
    if (bSuccess)
    {
        theStateInfo.text = @"• Success connected to server";
    }
}

// 用户登陆消息
- (void) OnAnyChatLogin:(int) dwUserId : (int) dwErrorCode
{
    onlineUserMArray = [NSMutableArray arrayWithCapacity:5];
    
    if(dwErrorCode == GV_ERR_SUCCESS)
    {
        [self updateLocalSettings];
        theOnLineLoginState = YES;
        theMyUserID = dwUserId;
        [self saveSettings];  //save correct configuration
        theStateInfo.text = [NSString stringWithFormat:@" Login successed. Self UserId: %d", dwUserId];
        [theLoginBtn setBackgroundImage:[UIImage imageNamed:@"btn_logout_01"] forState:UIControlStateNormal];
        
        if([theRoomNO.text length] == 0)
        {
            theRoomNO.text = [self GetRoomNO];
        }
        [AnyChatPlatform EnterRoom:[theRoomNO.text intValue] :@""];
    }
    else
    {
        theOnLineLoginState = NO;
        theStateInfo.text = [NSString stringWithFormat:@"• Login failed(ErrorCode:%i)",dwErrorCode];
    }
    
}

// 用户进入房间消息
- (void) OnAnyChatEnterRoom:(int) dwRoomId : (int) dwErrorCode
{
    if (dwErrorCode != 0)
    {
        theStateInfo.text = [NSString stringWithFormat:@"• Enter room failed(ErrorCode:%i)",dwErrorCode];
    }
    else
    {
        theOnLineLoginState = YES;
    }

    [onLineUserTableView reloadData];
}

// 房间在线用户消息
- (void) OnAnyChatOnlineUser:(int) dwUserNum : (int) dwRoomId
{
    onlineUserMArray = [self getOnlineUserArray];
    [onLineUserTableView reloadData];
}

// 用户进入房间消息
- (void) OnAnyChatUserEnterRoom:(int) dwUserId
{
    onlineUserMArray = [self getOnlineUserArray];
    [onLineUserTableView reloadData];
}

// 用户退出房间消息
- (void) OnAnyChatUserLeaveRoom:(int) dwUserId
{
    if (videoVC.iRemoteUserId == dwUserId ) {
        [videoVC FinishVideoChat];
        videoVC.iRemoteUserId = -1;
    }
    onlineUserMArray = [self getOnlineUserArray];
    [onLineUserTableView reloadData];
}

// 网络断开消息
- (void) OnAnyChatLinkClose:(int) dwErrorCode
{
    [videoVC FinishVideoChat];
    [AnyChatPlatform LeaveRoom:-1];
    [AnyChatPlatform Logout];
    theOnLineLoginState = NO;
    [onlineUserMArray removeAllObjects];
    [onLineUserTableView reloadData];
    
    theStateInfo.text = [NSString stringWithFormat:@"• OnLinkClose(ErrorCode:%i)",dwErrorCode];
    [theLoginBtn setBackgroundImage:[UIImage imageNamed:@"btn_login_01"] forState:UIControlStateNormal];
}


#pragma mark - Get & Save Settings Method

- (id) GetServerIP
{
    NSString* serverIP;
    NSString *documentPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *filePath = [documentPath stringByAppendingPathComponent:kAnyChatSettingsFileName];
    if([[NSFileManager defaultManager] fileExistsAtPath:filePath])
    {
        NSMutableArray* array = [[NSMutableArray alloc]initWithContentsOfFile:filePath];
        serverIP =  [array objectAtIndex:0];
        
        if([serverIP length] == 0)
        {
            theServerIP.text = @"demo.anychat.cn";
            serverIP = theServerIP.text;
        }
    }
    else
    {
        theServerIP.text = @"demo.anychat.cn";
        serverIP = theServerIP.text;
    }
    return serverIP;
}

- (id) GetServerPort
{
    NSString* serverPort;
    NSString *documentPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *filePath = [documentPath stringByAppendingPathComponent:kAnyChatSettingsFileName];
    if([[NSFileManager defaultManager] fileExistsAtPath:filePath])
    {
        NSMutableArray* array = [[NSMutableArray alloc]initWithContentsOfFile:filePath];
        serverPort = [array objectAtIndex:1];
        
        if([serverPort intValue] == 0 || [serverPort length] == 0)
        {
            theServerPort.text = @"8906";
            serverPort = theServerPort.text;
        }
    }
    else
    {
        theServerPort.text = @"8906";
        serverPort = theServerPort.text;
    }
    return serverPort;
}

- (id) GetUserName
{
    NSString* userName;
    NSString *documentPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *filePath = [documentPath stringByAppendingPathComponent:kAnyChatSettingsFileName];
    if([[NSFileManager defaultManager] fileExistsAtPath:filePath])
    {
        NSMutableArray* array = [[NSMutableArray alloc]initWithContentsOfFile:filePath];
        userName =  [array objectAtIndex:2];
        
        if([userName length] == 0)
        {
            theUserName.text = @"HelloAnyChat";
            userName = theServerIP.text;
        }
    }
    else
    {
        theUserName.text = @"HelloAnyChat";
        userName = theUserName.text;
    }
    return userName;
}

- (id) GetRoomNO
{
    NSString* roomNO;
    NSString *documentPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *filePath = [documentPath stringByAppendingPathComponent:kAnyChatSettingsFileName];
    if([[NSFileManager defaultManager] fileExistsAtPath:filePath])
    {
        NSMutableArray* array = [[NSMutableArray alloc]initWithContentsOfFile:filePath];
        roomNO = [array objectAtIndex:3];
        
        if([roomNO intValue] == 0 || [roomNO length] == 0)
        {
            theRoomNO.text = @"1";
            roomNO = theRoomNO.text;
        }
    }
    else
    {
        theRoomNO.text = @"1";
        roomNO = theRoomNO.text;
    }
    return roomNO;
}

- (void)saveSettings
{   // save settings to file
    NSString *documentPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *filePath = [documentPath stringByAppendingPathComponent:kAnyChatSettingsFileName];
    [[NSArray arrayWithObjects:theServerIP.text,theServerPort.text,theUserName.text,theRoomNO.text, nil] writeToFile:filePath atomically:YES];
}


#pragma mark - Instance Method

- (void)AnyChatNotifyHandler:(NSNotification*)notify
{
    NSDictionary* dict = notify.userInfo;
    [anyChat OnRecvAnyChatNotify:dict];
}

- (NSMutableArray *) getOnlineUserArray
{
    NSMutableArray *onLineUserList = [[NSMutableArray alloc] initWithArray:[AnyChatPlatform GetOnlineUser]];
    [onLineUserList insertObject:[NSString stringWithFormat:@"%i",self.theMyUserID] atIndex:0];
    return onLineUserList;
}

- (IBAction)hideKeyBoard
{
    [theServerIP resignFirstResponder];
    [theServerPort resignFirstResponder];
    [theUserName resignFirstResponder];
    [theRoomNO resignFirstResponder];
}

- (IBAction)OnLoginBtnClicked:(id)sender
{
    if (theOnLineLoginState == YES)
    {
        [self OnLogout];
    }
    else
    {
        [self OnLogin];
    }
}

- (void) OnLogin
{
    if (theOnLineLoginState == NO)
    {
        [self showLoadingAnimated];
        
        
        if([theServerIP.text length] == 0)
        {
            theServerIP.text = [self GetServerIP];
        }
        if([theServerPort.text length] == 0)
        {
            theServerPort.text = [self GetServerPort];
        }
        if([theUserName.text length] == 0)
        {
            theUserName.text = [self GetUserName];
        }
        [AnyChatPlatform Connect:theServerIP.text : [theServerPort.text intValue]];
        [AnyChatPlatform Login:theUserName.text : @""];
    }
    
    [self hideKeyBoard];
}

- (void) OnLogout
{
    if (theOnLineLoginState == YES)
    {
        [AnyChatPlatform LeaveRoom:-1];
        [AnyChatPlatform Logout];
        
        theOnLineLoginState = NO;
        [onlineUserMArray removeAllObjects];
        [onLineUserTableView reloadData];
        theStateInfo.text = @"• Logout Server.";
        [theLoginBtn setBackgroundImage:[UIImage imageNamed:@"btn_login_01"] forState:UIControlStateNormal];
    }
}

- (int)getRandomNumber:(int)from to:(int)to
{
    //  +1,result is [from to]; else is [from, to)!!!!!!!
    return (int)(from + (arc4random() % (to - from + 1)));
}

- (BOOL)prefersStatusBarHidden
{
    return YES;
}

#pragma mark - Animation Method

- (void)showLoadingAnimated
{
    HUD = [[MBProgressHUD alloc] initWithView:self.navigationController.view];
    [self.navigationController.view addSubview:HUD];
    
    HUD.delegate = self;
    HUD.dimBackground = YES;
    HUD.labelText = @"HelloAnyChat";
    HUD.detailsLabelText = @"Connecting...";
    
    [HUD showWhileExecuting:@selector(onLoginLoadingAnimatedRunTime) onTarget:self withObject:nil animated:YES];
}

- (void)onLoginLoadingAnimatedRunTime
{
    int theTimes = 0;
    while (theOnLineLoginState == NO && theTimes < 6)
    {
        sleep(1);
        theTimes++;
        
        if (theTimes == 5)
        {
            if (theOnLineLoginState == NO)
            {
                theStateInfo.text = @"Login timeout,please check the Network and Setting.";
            }
        }
    }
}


#pragma mark - Video Setting
// 更新本地参数设置
- (void) updateLocalSettings
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults synchronize];
    
    BOOL bUseP2P = [[defaults objectForKey:kUseP2P] boolValue];
    BOOL bUseServerVideoParam = [[defaults objectForKey:kUseServerParam] boolValue];
    int iVideoSolution =    [[defaults objectForKey:kVideoSolution] intValue];
    int iVideoBitrate =     [[defaults objectForKey:kVideoBitrate] intValue];
    int iVideoFrameRate =   [[defaults objectForKey:kVideoFrameRate] intValue];
    int iVideoPreset =      [[defaults objectForKey:kVideoPreset] intValue];
    int iVideoQuality =     [[defaults objectForKey:kVideoQuality] intValue];
    
    // P2P
    [AnyChatPlatform SetSDKOptionInt:BRAC_SO_NETWORK_P2PPOLITIC : (bUseP2P ? 1 : 0)];
    
    if(bUseServerVideoParam)
    {
        // 屏蔽本地参数，采用服务器视频参数设置
        [AnyChatPlatform SetSDKOptionInt:BRAC_SO_LOCALVIDEO_APPLYPARAM :0];
    }
    else
    {
        int iWidth, iHeight;
        switch (iVideoSolution) {
            case 0:     iWidth = 1280;  iHeight = 720;  break;
            case 1:     iWidth = 640;   iHeight = 480;  break;
            case 2:     iWidth = 480;   iHeight = 360;  break;
            case 3:     iWidth = 352;   iHeight = 288;  break;
            case 4:     iWidth = 192;   iHeight = 144;  break;
            default:    iWidth = 352;   iHeight = 288;  break;
        }
        [AnyChatPlatform SetSDKOptionInt:BRAC_SO_LOCALVIDEO_WIDTHCTRL :iWidth];
        [AnyChatPlatform SetSDKOptionInt:BRAC_SO_LOCALVIDEO_HEIGHTCTRL :iHeight];
        [AnyChatPlatform SetSDKOptionInt:BRAC_SO_LOCALVIDEO_BITRATECTRL :iVideoBitrate];
        [AnyChatPlatform SetSDKOptionInt:BRAC_SO_LOCALVIDEO_FPSCTRL :iVideoFrameRate];
        [AnyChatPlatform SetSDKOptionInt:BRAC_SO_LOCALVIDEO_PRESETCTRL :iVideoPreset];
        [AnyChatPlatform SetSDKOptionInt:BRAC_SO_LOCALVIDEO_QUALITYCTRL :iVideoQuality];
        
        // 采用本地视频参数设置，使参数设置生效
        [AnyChatPlatform SetSDKOptionInt:BRAC_SO_LOCALVIDEO_APPLYPARAM :1];
    }
    
}


#pragma mark - UI Controls

- (void)setUIControls
{
    [self.navigationController setNavigationBarHidden:YES];
    
    theRoomNO.text = [self GetRoomNO];
    theUserName.text = [self GetUserName];
    theServerIP.text = [self GetServerIP];
    theServerPort.text = [self GetServerPort];
    
    [theServerIP addTarget:self action:@selector(textFieldShouldReturn:) forControlEvents:UIControlEventEditingDidEndOnExit];
    [theServerPort addTarget:self action:@selector(textFieldShouldReturn:) forControlEvents:UIControlEventEditingDidEndOnExit];
    [theUserName addTarget:self action:@selector(textFieldShouldReturn:) forControlEvents:UIControlEventEditingDidEndOnExit];
    [theRoomNO addTarget:self action:@selector(textFieldShouldReturn:) forControlEvents:UIControlEventEditingDidEndOnExit];
    
    if ([[UIDevice currentDevice].systemVersion floatValue] < 7.0)
    {
        [[UIApplication sharedApplication] setStatusBarHidden:YES];
    }

    if (k_iPhone4)
    {
        onLineUserTableView.frame = CGRectMake(0, 240, 320, 210);
    }
    else if (k_iPhone5)
    {
        onLineUserTableView.frame = CGRectMake(0, 258, 320, 280);
    }
    
    [theVersion setText:[AnyChatPlatform GetSDKVersion]];
    [self prefersStatusBarHidden];
    
    UIView *footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kSelfView_Width, 70.0f)];
    onLineUserTableView.tableFooterView = footerView;
    
}


@end
