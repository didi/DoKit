//
//  DoraemonMultiCaseListViewController.m
//  DoraemonKit
//
//  Created by wzp on 2021/10/9.
//

#import "DoraemonMultiCaseListViewController.h"
#import <objc/runtime.h>
#import "UIView+Doraemon.h"
#import "UIViewController+Doraemon.h"
#import "DoraemonDefine.h"
#import "DoraemMultiMockLogic.h"
#import "NSArray+JSON.h"
#import "DoraemMultiCaseListModel.h"
#import <Masonry/Masonry.h>
@class DoraemonCaseListCell;
@interface DoraemonMultiCaseListViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (strong, nonatomic) UITableView *tableView;
@property (nonatomic, copy) NSArray <DoraemMultiCaseListModel *> *loadModelArray;

@end

@implementation DoraemonMultiCaseListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"一机多控 用例列表";
    self.navigationItem.leftBarButtonItems = nil;
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, self.view.doraemon_height) style:UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    [self.view addSubview:self.tableView];
    
    
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //跳转到列表里面去
    [DoraemMultiMockLogic  getMultiCaseListWithSus:^(id  _Nonnull responseObject) {
        if (![responseObject isKindOfClass:[NSDictionary class]]) {
            return;
        }
        NSArray *data = (NSArray *)responseObject[@"data"];
        self.loadModelArray = [NSArray arrayWithJsonArray:data modelClassName:NSStringFromClass([DoraemMultiCaseListModel class])];
        [self.tableView reloadData];
        
    } fail:^(NSError * _Nonnull error) {
     
        
    }];
    
}







#pragma mark ---UITableViewDelegate UITableViewDataSource----
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return _loadModelArray.count;
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    return  [DoraemonCaseListCell cellHeight];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    
    
    
    
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    
    static NSString *identifer = @"DoraemonCaseListCell";
    DoraemonCaseListCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[DoraemonCaseListCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    }
    DoraemMultiCaseListModel * mdoel = [self.loadModelArray objectAtIndex:indexPath.row];
    [cell renderUIWithData:mdoel];
    return  cell;
}

@end




@interface DoraemonCaseListCell()


@property (nonatomic, strong) UILabel *leftLabelFirst;
@property (nonatomic, strong) UILabel *leftLabelSecond;
@property (nonatomic, strong) UILabel *leftLabelThird;
@property (nonatomic, strong) UILabel *leftLabelFourth;
@property (nonatomic, strong) UIButton *selectBtn;
@end

@implementation DoraemonCaseListCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        self.leftLabelFirst =  [[UILabel alloc]init];
        self.leftLabelFirst.textColor = [UIColor redColor];
        self.leftLabelFirst.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)];
        [self.contentView addSubview:self.leftLabelFirst];
        
        self.leftLabelSecond =  [[UILabel alloc]init];
        self.leftLabelSecond.textColor = [UIColor redColor];
        self.leftLabelSecond.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)];
        [self.contentView addSubview:self.leftLabelSecond];
         
        self.leftLabelThird =  [[UILabel alloc]init];
        self.leftLabelThird.textColor = [UIColor doraemon_black_1];
        self.leftLabelThird.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)];
        [self.contentView addSubview:self.leftLabelThird];
        
        self.leftLabelFourth =  [[UILabel alloc]init];
        self.leftLabelFourth.textColor = [UIColor doraemon_black_1];
        self.leftLabelFourth.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)];
        [self.contentView addSubview:self.leftLabelFourth];
        
        // 布局
        [self.leftLabelFirst mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.mas_top).offset(5);
            make.left.equalTo(self.mas_left).offset(20);
            make.width.equalTo(@(DoraemonScreenWidth - 50));
            make.height.equalTo(@(20));
        }];
        
        [self.leftLabelSecond mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.leftLabelFirst.mas_bottom).offset(5);
            make.left.equalTo(self.mas_left).offset(20);
            make.width.equalTo(@(DoraemonScreenWidth - 50));
            make.height.equalTo(@(20));
        }];
        
        [self.leftLabelThird mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.leftLabelSecond.mas_bottom).offset(5);
            make.left.equalTo(self.mas_left).offset(20);
            make.width.equalTo(@(DoraemonScreenWidth - 50));
            make.height.equalTo(@(20));
        }];
        
        [self.leftLabelFourth mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.leftLabelThird.mas_bottom).offset(5);
            make.left.equalTo(self.mas_left).offset(20);
            make.width.equalTo(@(DoraemonScreenWidth - 50));
            make.height.equalTo(@(20));
        }];

    }
    return self;
    
}

- (void)renderUIWithData:(DoraemMultiCaseListModel *)model {
    
    self.leftLabelFirst.text =  [NSString stringWithFormat:@"用例名称:%@",model.caseName];
    self.leftLabelSecond.text = model.caseId;
    self.leftLabelThird.text = [NSString stringWithFormat:@"采集人:%@",model.personName];  ;   //采集人
    self.leftLabelFourth.text = [NSString stringWithFormat:@"采集时间:%@",model.createTime];  //采集时间
//
    
}
+ (CGFloat)cellHeight{
    return kDoraemonSizeFrom750_Landscape(200);
}

@end


