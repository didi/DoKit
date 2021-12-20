//
//  DoraemonMockCellSwitch.m
//  DoraemonKit
//
//  Created by didi on 2019/10/23.
//

#import "DoraemonMockDetailSwitch.h"
#import "DoraemonDefine.h"

@interface DoraemonMockDetailSwitch()

@property (nonatomic, strong) UIImageView *arrow;
@property (nonatomic, assign) CGFloat size;
@end

@implementation DoraemonMockDetailSwitch

- (void)needArrow{
    _size = kDoraemonSizeFrom750_Landscape(24);
    _arrow = [[UIImageView alloc] init];
    _arrow.image = [UIImage doraemon_xcassetImageNamed:@"doraemon_mock_detail_up"];
    _arrow.frame = CGRectMake(self.doraemon_width - _size*3, self.doraemon_height/2-_arrow.image.size.height/2, _arrow.image.size.width, _arrow.image.size.height);
    self.backgroundColor = [UIColor whiteColor];
    [self addSubview:_arrow];
}

- (void)setSwitchFrame{
    self.switchView.frame = CGRectMake(_arrow.doraemon_left - self.switchView.doraemon_width-_size*2, self.doraemon_height/2-self.switchView.doraemon_height/2, self.switchView.doraemon_width, self.switchView.doraemon_height);
}

- (void)setArrowDown:(BOOL)isDown{
    NSString *imgName = @"doraemon_mock_detail_up";
    if(isDown){
        imgName = @"doraemon_mock_detail_down";
    }
    _arrow.image = [UIImage doraemon_xcassetImageNamed:imgName];
    _arrow.doraemon_width = _arrow.image.size.width;
    _arrow.doraemon_height =  _arrow.image.size.height;
}



@end
