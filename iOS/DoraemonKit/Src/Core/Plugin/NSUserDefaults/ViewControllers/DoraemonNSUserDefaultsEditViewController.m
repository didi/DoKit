//
//  DoraemonNSUserDefaultsEditViewController.m
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/11/26.
//

#import "DoraemonNSUserDefaultsEditViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonNSUserDefaultsModel.h"

@interface DoraemonNSUserDefaultsEditViewController ()
@property (nonatomic, strong) UITextView *textView;
@end

@implementation DoraemonNSUserDefaultsEditViewController

- (instancetype)initWithModel: (DoraemonNSUserDefaultsModel *)model
{
    self = [super init];
    if (self) {
        self.model = model;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.textView = [[UITextView alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, self.view.doraemon_height-IPHONE_NAVIGATIONBAR_HEIGHT)];
    [self.view addSubview:self.textView];
    self.title = self.model.key;
    self.textView.text = [self.model.value description];
    
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(submit)];
    self.navigationItem.rightBarButtonItems = @[item];
    self.title = @"Edit";
}

- (void)submit {
    [[NSUserDefaults standardUserDefaults] setObject:self.textView.text forKey:_model.key];
    [self.navigationController popViewControllerAnimated:true];
}

@end
