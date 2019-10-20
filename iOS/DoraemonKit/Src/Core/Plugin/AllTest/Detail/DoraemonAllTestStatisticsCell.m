//
//  DoraemonAllTestStatisticsCell.m
//  AFNetworking
//
//  Created by didi on 2019/9/26.
//

#import "DoraemonAllTestStatisticsCell.h"
#import "UIView+Doraemon.h"
#import "DoraemonDefine.h"

@interface DoraemonAllTestStatisticsCell()

@property(nonatomic ,strong) UIImageView *upImage;
@property(nonatomic ,strong) UIImageView *downImage;
@property(nonatomic ,strong) UILabel *titleText;
@property(nonatomic ,strong) UILabel *upValue;
@property(nonatomic ,strong) UILabel *downValue;
@property(nonatomic ,strong) UILabel *averageText;
@property(nonatomic ,strong) UILabel *averageValue;
@property(nonatomic, assign) int height;
@property(nonatomic, assign) int fontSize;
@property(nonatomic, assign) int nextX;


@end

@implementation DoraemonAllTestStatisticsCell

- (UILabel *)titleText {
    if (!_titleText) {
        _nextX = kDoraemonSizeFrom750_Landscape(10);
        _titleText = [[UILabel alloc] initWithFrame:CGRectMake(_nextX, 0, self.doraemon_width/5, _height)];
        _titleText.textAlignment = NSTextAlignmentRight;
        _titleText.font = [UIFont systemFontOfSize:_fontSize];
        _titleText.adjustsFontSizeToFitWidth = YES;
        _nextX += _titleText.frame.size.width;
    }
    return _titleText;
}

- (UILabel *)upValue {
    if (!_upValue) {
        _upValue = [[UILabel alloc] initWithFrame:CGRectMake(_nextX, 0, self.doraemon_width/6, _height)];
        _upValue.textAlignment = NSTextAlignmentLeft;
        _upValue.font = [UIFont systemFontOfSize:_fontSize];
        _upValue.adjustsFontSizeToFitWidth = YES;
        _nextX += _upValue.frame.size.width;
    }
    return _upValue;
}

- (UILabel *)downValue {
    if (!_downValue) {
        _downValue = [[UILabel alloc] initWithFrame:CGRectMake(_nextX, 0, self.doraemon_width/6, _height)];
        _downValue.textAlignment = NSTextAlignmentLeft;
        _downValue.font = [UIFont systemFontOfSize:_fontSize];
        _downValue.adjustsFontSizeToFitWidth = YES;
        _nextX += _downValue.frame.size.width;
    }
    return _downValue;
}

- (UILabel *)averageText {
    if (!_averageText) {
        _averageText = [[UILabel alloc] initWithFrame:CGRectMake(_nextX, 0, self.doraemon_width/8, _height)];
        _averageText.textAlignment = NSTextAlignmentRight;
        _averageText.font = [UIFont systemFontOfSize:_fontSize];
        _averageText.adjustsFontSizeToFitWidth = YES;
        _nextX += _averageText.frame.size.width;
    }
    return _averageText;
}

- (UILabel *)averageValue {
    if (!_averageValue) {
        _averageValue = [[UILabel alloc] initWithFrame:CGRectMake(_nextX, 0, self.doraemon_width/5, _height)];
        _averageValue.textAlignment = NSTextAlignmentLeft;
        _averageValue.font = [UIFont systemFontOfSize:_fontSize];
        _averageValue.adjustsFontSizeToFitWidth = YES;
    }
    return _averageValue;
}


- (UIImageView *)upImage {
    if (!_upImage) {
        CGFloat size = kDoraemonSizeFrom750_Landscape(24);
        CGFloat sizeGo = kDoraemonSizeFrom750_Landscape(4);
        _nextX += kDoraemonSizeFrom750_Landscape(40);
        _upImage = [[UIImageView alloc] initWithFrame:CGRectMake(_nextX, sizeGo, size-sizeGo, size)];
        _upImage.image = [UIImage doraemon_imageNamed:@"doraemon_max"];
        _nextX += _upImage.frame.size.width;
    }
    return _upImage;
}

- (UIImageView *)downImage {
    if (!_downImage) {
        CGFloat size = kDoraemonSizeFrom750_Landscape(24);
        CGFloat sizeGo = kDoraemonSizeFrom750_Landscape(4);
        _downImage = [[UIImageView alloc] initWithFrame:CGRectMake(_nextX, sizeGo, size-sizeGo, size)];
        _downImage.image = [UIImage doraemon_imageNamed:@"doraemon_min"];
        _nextX += _upImage.frame.size.width;
    }
    return _downImage;
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor whiteColor];
        _height = kDoraemonSizeFrom750_Landscape(33);
        _fontSize = kDoraemonSizeFrom750_Landscape(24);
        [self addSubview:self.titleText];
        [self addSubview:self.upImage];
        [self addSubview:self.upValue];
        [self addSubview:self.downImage];
        [self addSubview:self.downValue];
        [self addSubview:self.averageText];
        [self addSubview:self.averageValue];
        NSString *averageText = DoraemonLocalizedString(@"平均值");
        _averageText.text = averageText;
    }
    return self;
}

- (void)update:(NSString *)title up:(NSString *)upValue down:(NSString *)downValue average:(NSString *)averageValue {
    NSString *endSimple = @"";
    NSArray *titleArray = [NSArray arrayWithObjects:@"downFlow",@"upFlow",@"memory",@"CPU",@"fps",nil];
    int index = (int)[titleArray indexOfObject:title];
    switch (index) {
        case 0:
            endSimple = @"B";
            self.titleText.text = [NSString stringWithFormat:@"%@ :", DoraemonLocalizedString(@"下行流量")];
            break;
        case 1:
            endSimple = @"B";
            self.titleText.text = [NSString stringWithFormat:@"%@ :", DoraemonLocalizedString(@"上行流量")];
            break;
        case 2:
            endSimple = @"M";
            self.titleText.text = [NSString stringWithFormat:@"%@ :", DoraemonLocalizedString(@"内存")];
            break;
        case 3:
            endSimple = @"%";
            self.titleText.text = [NSString stringWithFormat:@"%@ :", title];
            break;
        case 4:
            self.titleText.text = [NSString stringWithFormat:@"%@ :", @"FPS"];
            break;
        default:
            break;
    }
    self.upValue.text = [NSString stringWithFormat:@" %@%@", upValue ,endSimple];
    self.downValue.text = [NSString stringWithFormat:@" %@%@", downValue, endSimple];
    self.averageValue.text = [NSString stringWithFormat:@" : %@%@", averageValue, endSimple];
}

@end
