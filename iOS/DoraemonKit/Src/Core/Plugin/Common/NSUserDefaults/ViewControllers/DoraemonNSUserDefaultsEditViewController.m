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
@property (nonatomic, strong) UITextView *keyTextView;
@property (nonatomic, strong) UITextView *valueTextView;
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
    
    self.keyTextView = [[UITextView alloc] init];
    self.keyTextView.editable = NO;
    self.keyTextView.font = [UIFont systemFontOfSize:16];
    [self.view addSubview:self.keyTextView];
    self.keyTextView.text = self.model.key;
    CGFloat keyMaxWidth = self.view.doraemon_width;
    CGFloat keyMaxHeight = 60;
    CGSize keySize = [self.keyTextView sizeThatFits:CGSizeMake(keyMaxWidth, keyMaxHeight)];
    self.keyTextView.frame = CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT + 10, keyMaxWidth, keySize.height + 1);
    
    self.valueTextView = [[UITextView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(self.keyTextView.frame) + 10, self.view.doraemon_width, 300)];
    [self.view addSubview:self.valueTextView];
    self.valueTextView.layer.borderWidth = 0.5;
    self.valueTextView.layer.borderColor = [[UIColor lightGrayColor] CGColor];
    self.title = self.model.key;
    self.valueTextView.text = [self.model.value description];
    
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(submit)];
    self.navigationItem.rightBarButtonItems = @[item];
    self.title = @"Edit";
}

- (void)submit {
    [[NSUserDefaults standardUserDefaults] setObject:self.valueTextView.text forKey:_model.key];
    [self.navigationController popViewControllerAnimated:true];
}

@end
