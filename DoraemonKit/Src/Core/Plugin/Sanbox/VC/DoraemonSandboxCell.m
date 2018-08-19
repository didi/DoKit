//
//  DoraemonSandboxCell.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import "DoraemonSandboxCell.h"
#import "DoraemonSandboxModel.h"

@implementation DoraemonSandBoxCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    return self;
}

- (void)renderUIWithData : (DoraemonSandboxModel *)model{
    self.textLabel.text = model.name;
}

@end
