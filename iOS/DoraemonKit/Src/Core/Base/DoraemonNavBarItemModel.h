//
//  DoraemonNavBarItemModel.h
//  DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

typedef NS_ENUM(NSUInteger, DoraemonNavBarItemType) {
    DoraemonNavBarItemTypeText = 0,
    DoraemonNavBarItemTypeImage,
};

@interface DoraemonNavBarItemModel : NSObject

@property (nonatomic, assign) DoraemonNavBarItemType type;
@property (nonatomic, copy) NSString *text;
@property (nonatomic, strong) UIImage *image;
@property (nonatomic, strong) UIColor *textColor;
@property (nonatomic, assign) SEL selector;

- (instancetype)initWithText:(NSString *)text color:(UIColor *)color selector:(SEL)selector;
- (instancetype)initWithImage:(UIImage *)image selector:(SEL)selector;

@end
