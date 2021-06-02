#import <DoraemonKit/DKPickerView.h>
#import <DoraemonKit/UIImage+Doraemon.h>

NS_ASSUME_NONNULL_BEGIN

@interface DKPickerView ()

- (void)pickerViewInit;

@end

NS_ASSUME_NONNULL_END

@implementation DKPickerView

- (void)pickerViewInit {
    self.overflow = YES;
    self.backgroundColor = UIColor.clearColor;
    self.layer.cornerRadius = MIN(self.bounds.size.width, self.bounds.size.height) / 2.0;
    UIImageView *imageView = [[UIImageView alloc] initWithImage:[UIImage doraemon_xcassetImageNamed:@"doraemon_visual"]];
    imageView.frame = self.bounds;
    [self addSubview:imageView];
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    [self pickerViewInit];

    return self;
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    if (self = [super initWithCoder:coder]) {
        [self pickerViewInit];
    }

    return self;
}

@end
