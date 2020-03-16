//
//  DoraemonMockUploadCell.m
//  AFNetworking
//
//  Created by didi on 2019/11/15.
//

#import "DoraemonMockUploadCell.h"
#import "DoraemonMockManager.h"
#import "DoraemonNetworkUtil.h"
#import "DoraemonManager.h"
#import "DoraemonToastUtil.h"

@interface DoraemonMockUploadCell()

@property (nonatomic, strong) UIView *containerView;
@property (nonatomic, strong) UIButton *previewBtn;
@property (nonatomic, strong) UIButton *uploadBtn;

@end

@implementation DoraemonMockUploadCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        _containerView = [[UIView alloc] init];
        [self.contentView addSubview:_containerView];
        
        _previewBtn = [[UIButton alloc] init];
        [_previewBtn setTitle:DoraemonLocalizedString(@"数据预览") forState:UIControlStateNormal];
        [_previewBtn setTitleColor:[UIColor doraemon_blue] forState:UIControlStateNormal];
        [_previewBtn addTarget:self action:@selector(preview) forControlEvents:UIControlEventTouchUpInside];
        [_containerView addSubview:_previewBtn];
        
        _uploadBtn = [[UIButton alloc] init];
        [_uploadBtn setTitle:DoraemonLocalizedString(@"上传") forState:UIControlStateNormal];
        [_uploadBtn setTitleColor:[UIColor doraemon_blue] forState:UIControlStateNormal];
        [_uploadBtn addTarget:self action:@selector(upload) forControlEvents:UIControlEventTouchUpInside];
        [_containerView addSubview:_uploadBtn];
    }
    return self;
}

- (void)renderCellWithData:(DoraemonMockBaseModel *)model{
    [super renderCellWithData: model];
    if(!model||!model.expand){
        _containerView.hidden = YES;
        return ;
    }else{
        _containerView.hidden = NO;
    }
    
    [_previewBtn sizeToFit];
    _previewBtn.frame = CGRectMake(0, 0, _previewBtn.doraemon_width, _previewBtn.doraemon_height);
    [_uploadBtn sizeToFit];
    _uploadBtn.frame = CGRectMake(_previewBtn.doraemon_right+kDoraemonSizeFrom750_Landscape(48), 0, _uploadBtn.doraemon_width, _uploadBtn.doraemon_height);
    
    _containerView.frame = CGRectMake(kDoraemonSizeFrom750_Landscape(32), self.infoLabel.doraemon_bottom, DoraemonScreenWidth-kDoraemonSizeFrom750_Landscape(32)*2, _previewBtn.doraemon_height);
}

+ (CGFloat)cellHeightWith:(DoraemonMockBaseModel *)model{
    CGFloat cellHeight = [super cellHeightWith:model];
    
    if (model && model.expand){
        cellHeight += kDoraemonSizeFrom750_Landscape(32);
        cellHeight += kDoraemonSizeFrom750_Landscape(32);
    }

    return cellHeight;
}

- (void)preview{
    DoraemonMockUpLoadModel *upload = (DoraemonMockUpLoadModel *)self.model;
    if (self.delegate && [self.delegate respondsToSelector:@selector(previewClick:)]) {
        [self.delegate previewClick:upload];
    }
}

- (void)upload{
    DoraemonMockUpLoadModel *upload = (DoraemonMockUpLoadModel *)self.model;
    [[DoraemonMockManager sharedInstance] uploadSaveData:upload atView:self];
}

- (NSString *)convertToJsonData:(NSDictionary *)dict
{
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:&error];
    NSString *jsonString;

    if (!jsonData) {
        DoKitLog(@"%@",error);
    } else {
        jsonString = [[NSString alloc]initWithData:jsonData encoding:NSUTF8StringEncoding];
    }

    NSMutableString *mutStr = [NSMutableString stringWithString:jsonString];

    NSRange range = {0,jsonString.length};

    //去掉字符串中的空格
    [mutStr replaceOccurrencesOfString:@" " withString:@"" options:NSLiteralSearch range:range];

    NSRange range2 = {0,mutStr.length};

    //去掉字符串中的换行符
    [mutStr replaceOccurrencesOfString:@"\n" withString:@"" options:NSLiteralSearch range:range2];

    return mutStr;
}

#pragma mark -- DoraemonSwitchViewDelegate
- (void)changeSwitchOn:(BOOL)on sender:(id)sender{
    self.model.selected = on;
    
    // 1、是否开启网络拦截功能 : mock列表中只要有一个选中就需要打开mock功能
    BOOL needMockOn = NO;
    for (DoraemonMockAPIModel *api in [DoraemonMockManager sharedInstance].upLoadArray) {
        if (api.selected) {
            needMockOn = YES;
        }
    }
    [DoraemonMockManager sharedInstance].mock = needMockOn;
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(cellSwitchClick)]) {
        [self.delegate cellSwitchClick];
    }
}

@end
