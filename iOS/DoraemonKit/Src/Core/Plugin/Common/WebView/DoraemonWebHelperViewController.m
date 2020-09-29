//
//  DoraemonWebHelperViewController.m
//  AFNetworking
//
//  Created by 刘骁阳 on 2020/9/27.
//

#import "DoraemonWebHelperViewController.h"
#import "DoraemonDefine.h"

#define Doraemon_JS_HOOK @"Doraemon_JS_HOOK"
#define Doraemon_vConsole @"Doraemon_vConsole"
#define Doraemon_WEB_TABLE_ID @"Doraemon_WEB_TABLE_ID"

static __weak WKWebView *hookedWebView;
static NSArray<WKUserScript *> *originUserScripts;
static NSString *wkJSBridgeCode = nil;
static NSString *vConsoleCode = nil;

static NSMutableDictionary *localStorage;
static NSMutableDictionary *sessionStorage;

@interface DoraemonWebHelperViewController () <WKScriptMessageHandler, UITableViewDataSource, UITableViewDelegate>

@property (nonatomic, strong) UISegmentedControl *seg;
@property (nonatomic, strong) UITableView *tableView;

@end

@implementation DoraemonWebHelperViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = DoraemonLocalizedString(@"h5助手");
    
    
    if (!localStorage) {
        localStorage = @{}.mutableCopy;
    }
    
    if (!sessionStorage) {
        sessionStorage = @{}.mutableCopy;
    }
    
    CGFloat aviW = UIScreen.mainScreen.bounds.size.width - 30;
    CGFloat aviH = UIScreen.mainScreen.bounds.size.height;
    
    UILabel *lab_1 = [[UILabel alloc] init];
    lab_1.textColor = UIColor.grayColor;
    lab_1.font = [UIFont systemFontOfSize:25];
    lab_1.frame = CGRectMake(15, 100, aviW, 70);
    lab_1.numberOfLines = 0;
    lab_1.adjustsFontSizeToFitWidth = YES;
    [self.view addSubview:lab_1];
    
    UILabel *lab_2 = [[UILabel alloc] init];
    lab_2.textColor = UIColor.redColor;
    lab_2.font = [UIFont systemFontOfSize:14];
    lab_2.frame = CGRectMake(15, 170, aviW, 40);
    lab_2.numberOfLines = 0;
    lab_2.text = DoraemonLocalizedString(@"js hook功能需要在网页加载时注入，改变状态会自动重载网页");
    [self.view addSubview:lab_2];
    
    UILabel *lab_3 = [[UILabel alloc] init];
    lab_3.font = [UIFont systemFontOfSize:20];
    lab_3.frame = CGRectMake(15, 220, 100, 30);
    lab_3.numberOfLines = 0;
    lab_3.text = @"vConsole";
    [self.view addSubview:lab_3];
    
    UIButton *trigger = [UIButton buttonWithType:UIButtonTypeCustom];
    trigger.frame = CGRectMake(15 + aviW - 100, 220, 100, 30);
    [trigger setTitle:@"trigger" forState:UIControlStateNormal];
    [trigger setTitleColor:UIColor.doraemon_blue forState:UIControlStateNormal];
    trigger.layer.borderColor = UIColor.doraemon_blue.CGColor;
    trigger.layer.borderWidth = 1;
    trigger.layer.cornerRadius = 4;
    [self.view addSubview:trigger];
    [trigger addTarget:self action:@selector(createVConsole) forControlEvents:UIControlEventTouchUpInside];
    
    UILabel *lab_4 = [[UILabel alloc] init];
    lab_4.font = [UIFont systemFontOfSize:20];
    lab_4.frame = CGRectMake(15, 260, 100, 30);
    lab_4.numberOfLines = 0;
    lab_4.text = @"js hook";
    [self.view addSubview:lab_4];
    
    UISwitch *sw_js = [[UISwitch alloc] init];
    sw_js.onTintColor = UIColor.doraemon_blue;
    [sw_js setOn:[self jsHook]];
    sw_js.frame = CGRectMake(15 + aviW - 51, 260, 0, 0);
    [self.view addSubview:sw_js];
    [sw_js addTarget:self action:@selector(swJSChanged:) forControlEvents:UIControlEventValueChanged];
    
    if (self.webView) {
        lab_1.text = self.webView.URL.absoluteString;
        
        if ([self jsHook]) {
            [self injectJsHookCode];
        }
        
        UISegmentedControl *seg = [[UISegmentedControl alloc] initWithItems:@[@"LocalStorage", @"SessionStorage"]];
        [seg setTitleTextAttributes:@{NSForegroundColorAttributeName : UIColor.doraemon_blue} forState:UIControlStateSelected];
        seg.frame = CGRectMake(15, 300, aviW, 40);
        seg.selectedSegmentIndex = 0;
        [seg addTarget:self action:@selector(segSelected:) forControlEvents:UIControlEventValueChanged];
        [self.view addSubview:seg];
        self.seg = seg;
        
        UITableView *tableView = [[UITableView alloc] initWithFrame:CGRectMake(15, 345, aviW, aviH - 355) style:UITableViewStylePlain];
        [tableView setTableFooterView:[UIView new]];
        [tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:Doraemon_WEB_TABLE_ID];
        tableView.dataSource = self;
        tableView.delegate = self;
        [self.view addSubview:tableView];
        self.tableView = tableView;
    } else {
        lab_1.text = DoraemonLocalizedString(@"当前页面不存在WKWebView");
    }
}

- (void)injectJsHookCode {
    if (self.webView == hookedWebView) {
        return;
    }
    hookedWebView = self.webView;
    
    WKUserContentController *userContentController = self.webView.configuration.userContentController;
    
    if (!userContentController) {
        userContentController = [[WKUserContentController alloc] init];
        self.webView.configuration.userContentController = userContentController;
    }
    
    originUserScripts = userContentController.userScripts;
    
    WKUserScript *wkuScript = [[WKUserScript alloc] initWithSource:[self jshookCode]
                                                          injectionTime:WKUserScriptInjectionTimeAtDocumentStart
                                                       forMainFrameOnly:NO];
    
    [userContentController addUserScript:wkuScript];
    [userContentController addScriptMessageHandler:self name:@"handleJSMessage"];
    
    [self.webView reload];
}

- (void)recoverUserScripts {
    hookedWebView = nil;
    localStorage = @{}.mutableCopy;
    sessionStorage = @{}.mutableCopy;
    
    WKUserContentController *userContentController = self.webView.configuration.userContentController;
    [userContentController removeAllUserScripts];
    [originUserScripts enumerateObjectsUsingBlock:^(WKUserScript * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        [userContentController addUserScript:obj];
    }];
    [userContentController removeScriptMessageHandlerForName:@"handleJSMessage"];
    
    [self.webView reload];
}

- (void)createVConsole {
    if (self.webView) {
        [self.webView evaluateJavaScript:[NSString stringWithFormat:@"if (vConsole == undefined) { %@; var vConsole = new VConsole(); }", [self vConsoleCode]]
                       completionHandler:^(id _Nullable obj, NSError * _Nullable error) {
            if (!error) {
                [self leftNavBackClick:nil];
            } else {
                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Error" message:error.localizedDescription preferredStyle:UIAlertControllerStyleAlert];
                UIAlertAction *ok = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                }];
                [alert addAction:ok];
                [self presentViewController:alert animated:YES completion:nil];
            }
        }];
    }
}

- (void)swJSChanged:(UISwitch *)sender {
    [self setJsHook:sender.on];
    if (self.webView) {
        if(sender.on) {
            [self injectJsHookCode];
        } else {
            [self recoverUserScripts];
        }
    }
}

- (void)userContentController:(WKUserContentController *)userContentController didReceiveScriptMessage:(WKScriptMessage *)message {
    if ([message.body isKindOfClass:[NSString class]] && [message.name isEqualToString:@"handleJSMessage"]) {
        NSError *error;
        id jsMessages = [self _deserializeMessageJSON:message.body withError:&error];
        
        if ([jsMessages isKindOfClass:[NSArray class]]) {
            for (NSDictionary *message in jsMessages) {
                //                NSLog(@"[Doraemon js hook]: %@", message);
                
                NSString *command = message[@"command"];
                NSDictionary *params = message[@"params"];
                if ([command isEqualToString:@"synchronizeLocalStorage"]) {
                    [localStorage addEntriesFromDictionary:params];
                    [self.tableView reloadData];
                } else if ([command isEqualToString:@"synchronizeSessionStorage"]) {
                    [sessionStorage addEntriesFromDictionary:params];
                    [self.tableView reloadData];
                }
            }
        }
    }
}

- (NSString *)_serializeMessage:(id)message {
    return [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:message
                                                                          options:0
                                                                            error:nil]
                                 encoding:NSUTF8StringEncoding];
}

- (id)_deserializeMessageJSON:(NSString *)messageJSON withError:(NSError **)error {
    return [NSJSONSerialization JSONObjectWithData:[messageJSON dataUsingEncoding:NSUTF8StringEncoding]
                                           options:NSJSONReadingAllowFragments
                                             error:error];
}

- (NSString *)jshookCode {
    if (!wkJSBridgeCode) {
        NSString *bundle = @"DoraemonKit.bundle/jsHook";
        NSString *path = [[NSBundle mainBundle] pathForResource:bundle ofType:@"js"];
        wkJSBridgeCode = [NSString stringWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil];
    }
    
    return wkJSBridgeCode;
}

- (NSString *)vConsoleCode {
    if (!vConsoleCode) {
        NSString *bundle = @"DoraemonKit.bundle/vconsole.min";
        NSString *path = [[NSBundle mainBundle] pathForResource:bundle ofType:@"js"];
        vConsoleCode = [NSString stringWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil];
    }
    
    return vConsoleCode;
}

- (void)segSelected:(UISegmentedControl *)sender {
    [self.tableView reloadData];
}

- (BOOL)needBigTitleView{
    return YES;
}

- (void)setJsHook:(BOOL)value {
    [[NSUserDefaults standardUserDefaults] setBool:value forKey:Doraemon_JS_HOOK];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (BOOL)jsHook {
    return [[NSUserDefaults standardUserDefaults] boolForKey:Doraemon_JS_HOOK];
}

#pragma mark - UITableViewDataSource

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (self.seg.selectedSegmentIndex) {
        case 0:
            return localStorage.count;
            break;
        case 1:
            return sessionStorage.count;
            break;
        default:
            return 0;
            break;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:Doraemon_WEB_TABLE_ID];;
    NSString *key = @"";
    NSString *value = @"";
    switch (self.seg.selectedSegmentIndex) {
        case 0:
            key = localStorage.allKeys[indexPath.row];
            value = localStorage[key];
            break;
        case 1:
            key = sessionStorage.allKeys[indexPath.row];
            value = sessionStorage[key];
            break;
        default:
            break;
    }
    
    cell.textLabel.text = key;
    cell.textLabel.textColor = UIColor.doraemon_orange;
    cell.textLabel.numberOfLines = 1;
    cell.detailTextLabel.text = value;
    cell.detailTextLabel.numberOfLines = 2;
    
    return cell;
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 60;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString *key = @"";
    NSString *value = @"";
    switch (self.seg.selectedSegmentIndex) {
        case 0:
            key = localStorage.allKeys[indexPath.row];
            value = localStorage[key];
            break;
        case 1:
            key = sessionStorage.allKeys[indexPath.row];
            value = sessionStorage[key];
            break;
        default:
            break;
    }
    
    NSString *message = [NSString stringWithFormat:@"Key:\n%@\n\nValue:\n%@", key, value];
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Detail" message:message preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *copy = [UIAlertAction actionWithTitle:@"Copy" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
        pasteboard.string = message;
    }];
    UIAlertAction *ok = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
    }];
    [alert addAction:copy];
    [alert addAction:ok];
    [self presentViewController:alert animated:YES completion:nil];
}

@end
