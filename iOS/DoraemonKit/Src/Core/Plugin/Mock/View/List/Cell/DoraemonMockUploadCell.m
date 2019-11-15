//
//  DoraemonMockUploadCell.m
//  AFNetworking
//
//  Created by didi on 2019/11/15.
//

#import "DoraemonMockUploadCell.h"

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
        [_previewBtn setTitle:@"数据预览" forState:UIControlStateNormal];
        [_previewBtn setTitleColor:[UIColor doraemon_blue] forState:UIControlStateNormal];
        [_previewBtn addTarget:self action:@selector(preview) forControlEvents:UIControlEventTouchUpInside];
        [_containerView addSubview:_previewBtn];
        
        _uploadBtn = [[UIButton alloc] init];
        [_uploadBtn setTitle:@"上传" forState:UIControlStateNormal];
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
    
    _containerView.frame = CGRectMake(kDoraemonSizeFrom750_Landscape(32), self.infoLabel.doraemon_bottom, DoraemonScreenWidth-kDoraemonSizeFrom750_Landscape(32)*2, kDoraemonSizeFrom750_Landscape(32));
    [_previewBtn sizeToFit];
    _previewBtn.frame = CGRectMake(0, 0, _previewBtn.doraemon_width, _previewBtn.doraemon_height);
    [_uploadBtn sizeToFit];
    _uploadBtn.frame = CGRectMake(_previewBtn.doraemon_right+kDoraemonSizeFrom750_Landscape(48), 0, _uploadBtn.doraemon_width, _uploadBtn.doraemon_height);
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
    
}

- (void)upload{
    
}

#pragma mark -- DoraemonSwitchViewDelegate
- (void)changeSwitchOn:(BOOL)on sender:(id)sender{
    self.model.selected = on;
    if (self.delegate && [self.delegate respondsToSelector:@selector(cellSwitchClick)]) {
        [self.delegate cellSwitchClick];
    }
}

@end
