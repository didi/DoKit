//
//  DoraemonDBTableViewController.m
//  AFNetworking
//
//  Created by yixiang on 2019/3/31.
//

#import "DoraemonDBTableViewController.h"
#import "DoraemonDBManager.h"

@interface DoraemonDBTableViewController ()

@end

@implementation DoraemonDBTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = [DoraemonDBManager shareManager].tableName;
    
    
}

@end
