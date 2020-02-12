//
//  NSObject+DoraemonHierarchy.m
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "NSObject+DoraemonHierarchy.h"
#import "UIViewController+DoraemonHierarchy.h"
#import "DoraemonHierarchyFormatterTool.h"
#import "DoraemonHierarchyCategoryModel.h"
#import "DoraemonHierarchyCellModel.h"
#import "UIColor+DoraemonHierarchy.h"
#import "DoraemonEnumDescription.h"
#import "DoraemonHierarchyHelper.h"
#import "UIColor+Doraemon.h"
#import "DoraemonDefine.h"

NSNotificationName const DoraemonHierarchyChangeNotificationName = @"DoraemonHierarchyChangeNotificationName";

@implementation NSObject (DoraemonHierarchy)

#pragma mark - Public
- (NSArray <DoraemonHierarchyCategoryModel *>*)doraemon_hierarchyCategoryModels {
    
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Class Name" detailTitle:NSStringFromClass(self.class)] noneInsets];
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Address" detailTitle:[NSString stringWithFormat:@"%p",self]] noneInsets];
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Description" detailTitle:self.description] noneInsets];
    [settings addObject:model3];
    
    return @[[[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Object" items:settings]];
}

- (void)doraemon_showIntAlertAndAutomicSetWithKeyPath:(NSString *)keyPath {
    __weak typeof(self) weakSelf = self;
    [self doraemon_showTextFieldAlertWithText:[NSString stringWithFormat:@"%@",[self valueForKeyPath:keyPath]] handler:^(NSString * _Nullable newText) {
        [weakSelf setValue:@([newText integerValue]) forKeyPath:keyPath];
    }];
}

- (void)doraemon_showFrameAlertAndAutomicSetWithKeyPath:(NSString *)keyPath {
    __block NSValue *value = [self valueForKeyPath:keyPath];
    __weak typeof(self) weakSelf = self;
    [self doraemon_showTextFieldAlertWithText:NSStringFromCGRect([value CGRectValue]) handler:^(NSString * _Nullable newText) {
        [weakSelf setValue:[NSValue valueWithCGRect:[weakSelf doraemon_rectFromString:newText originalRect:[value CGRectValue]]] forKeyPath:keyPath];
    }];
}

- (void)doraemon_showColorAlertAndAutomicSetWithKeyPath:(NSString *)keyPath {
    __block UIColor *color = [self valueForKeyPath:keyPath];
    if (color && ![color isKindOfClass:[UIColor class]]) {
        return;
    }
    __weak typeof(self) weakSelf = self;
    [self doraemon_showTextFieldAlertWithText:[color doraemon_HexString] handler:^(NSString * _Nullable newText) {
        [weakSelf setValue:[weakSelf doraemon_colorFromString:newText originalColor:color] forKeyPath:keyPath];
    }];
}

- (void)doraemon_showFontAlertAndAutomicSetWithKeyPath:(NSString *)keyPath {
    __block UIFont *font = [self valueForKeyPath:keyPath];
    __weak typeof(self) weakSelf = self;
    [self doraemon_showTextFieldAlertWithText:[DoraemonHierarchyFormatterTool formatNumber:@(font.pointSize)] handler:^(NSString * _Nullable newText) {
        [weakSelf setValue:[font fontWithSize:[newText doubleValue]] forKeyPath:keyPath];
    }];
}

- (UIColor *)doraemon_hashColor {
    CGFloat hue = ((self.hash >> 4) % 256) / 255.0;
    return [UIColor colorWithHue:hue saturation:1.0 brightness:1.0 alpha:1.0];
}

#pragma mark - Primary
- (NSString *)doraemon_hierarchyColorDescription:(UIColor *_Nullable)color {
    if (!color) {
        return @"<nil color>";
    }
    
    CGFloat r = [color red];
    CGFloat g = [color green];
    CGFloat b = [color blue];
    CGFloat a = [color alpha];
    NSString *rgb = [NSString stringWithFormat:@"R:%@ G:%@ B:%@ A:%@", [DoraemonHierarchyFormatterTool formatNumber:@(r)], [DoraemonHierarchyFormatterTool formatNumber:@(g)], [DoraemonHierarchyFormatterTool formatNumber:@(b)], [DoraemonHierarchyFormatterTool formatNumber:@(a)]];
    
    NSString *colorName = [color doraemon_systemColorName];
    
    return colorName ? [rgb stringByAppendingFormat:@"\n%@",colorName] : [rgb stringByAppendingFormat:@"\n%@",[color doraemon_HexString]];
}

- (UIColor *)doraemon_colorFromString:(NSString *)string originalColor:(UIColor *)color {
    UIColor *newColor = [UIColor doraemon_colorWithHexString:string];
    if (!newColor) {
        return color;
    }
    return newColor;
}

- (NSString *)doraemon_hierarchyBoolDescription:(BOOL)flag {
    return flag ? @"On" : @"Off";
}

- (NSString *)doraemon_hierarchyImageDescription:(UIImage *)image {
    return image ? image.description : @"No image";
}

- (NSString *)doraemon_hierarchyObjectDescription:(NSObject *)obj {
    NSString *text = @"<null>";
    if (obj) {
        text = [NSString stringWithFormat:@"%@",obj];
    }
    if ([text length] == 0) {
        text = @"<empty string>";
    }
    return text;
}

- (NSString *)doraemon_hierarchyDateDescription:(NSDate *)date {
    if (!date) {
        return @"<null>";
    }
    return [DoraemonHierarchyFormatterTool stringFromDate:date] ?: @"<null>";
}

- (NSString *)doraemon_hierarchyTextDescription:(NSString *)text {
    if (text == nil) {
        return @"<nil>";
    }
    if ([text length] == 0) {
        return @"<empty string>";
    }
    return text;
}

- (NSString *)doraemon_hierarchyPointDescription:(CGPoint)point {
    return [NSString stringWithFormat:@"X: %@   Y: %@",[DoraemonHierarchyFormatterTool formatNumber:@(point.x)],[DoraemonHierarchyFormatterTool formatNumber:@(point.y)]];
}

- (CGPoint)doraemon_pointFromString:(NSString *)string orginalPoint:(CGPoint)point {
    CGPoint newPoint = CGPointFromString(string);
    return newPoint;
}

- (NSString *)doraemon_hierarchySizeDescription:(CGSize)size {
    return [NSString stringWithFormat:@"W: %@   H: %@",[DoraemonHierarchyFormatterTool formatNumber:@(size.width)], [DoraemonHierarchyFormatterTool formatNumber:@(size.height)]];
}

- (CGRect)doraemon_rectFromString:(NSString *)string originalRect:(CGRect)rect {
    CGRect newRect = CGRectFromString(string);
    if (CGRectEqualToRect(newRect, CGRectZero) && ![string isEqualToString:NSStringFromCGRect(CGRectZero)]) {
        // Wrong text.
        return rect;
    }
    return newRect;
}

- (CGSize)doraemon_sizeFromString:(NSString *)string originalSize:(CGSize)size {
    CGSize newSize = CGSizeFromString(string);
    if (CGSizeEqualToSize(newSize, CGSizeZero) && ![string isEqualToString:NSStringFromCGSize(CGSizeZero)]) {
        // Wrong text.
        return size;
    }
    return newSize;
}

- (NSString *)doraemon_hierarchyInsetsTopBottomDescription:(UIEdgeInsets)insets {
    return [NSString stringWithFormat:@"top %@    bottom %@",[DoraemonHierarchyFormatterTool formatNumber:@(insets.top)], [DoraemonHierarchyFormatterTool formatNumber:@(insets.bottom)]];
}

- (UIEdgeInsets)doraemon_insetsFromString:(NSString *)string originalInsets:(UIEdgeInsets)insets {
    UIEdgeInsets newInsets = UIEdgeInsetsFromString(string);
    if (UIEdgeInsetsEqualToEdgeInsets(newInsets, UIEdgeInsetsZero) && ![string isEqualToString:NSStringFromUIEdgeInsets(UIEdgeInsetsZero)]) {
        // Wrong text.
        return insets;
    }
    return newInsets;
}

- (NSString *)doraemon_hierarchyInsetsLeftRightDescription:(UIEdgeInsets)insets {
    return [NSString stringWithFormat:@"left %@    right %@",[DoraemonHierarchyFormatterTool formatNumber:@(insets.left)], [DoraemonHierarchyFormatterTool formatNumber:@(insets.right)]];
}

- (NSString *)doraemon_hierarchyOffsetDescription:(UIOffset)offset {
    return [NSString stringWithFormat:@"h %@   v %@",[DoraemonHierarchyFormatterTool formatNumber:@(offset.horizontal)], [DoraemonHierarchyFormatterTool formatNumber:@(offset.vertical)]];
}

- (void)doraemon_showDoubleAlertAndAutomicSetWithKeyPath:(NSString *)keyPath {
    __weak typeof(self) weakSelf = self;
    [self doraemon_showTextFieldAlertWithText:[DoraemonHierarchyFormatterTool formatNumber:[self valueForKeyPath:keyPath]] handler:^(NSString * _Nullable newText) {
        [weakSelf setValue:@([newText doubleValue]) forKeyPath:keyPath];
    }];
}

- (void)doraemon_showPointAlertAndAutomicSetWithKeyPath:(NSString *)keyPath {
    __block NSValue *value = [self valueForKeyPath:keyPath];
    __weak typeof(self) weakSelf = self;
    [self doraemon_showTextFieldAlertWithText:NSStringFromCGPoint([value CGPointValue]) handler:^(NSString * _Nullable newText) {
        [weakSelf setValue:[NSValue valueWithCGPoint:[weakSelf doraemon_pointFromString:newText orginalPoint:[value CGPointValue]]] forKeyPath:keyPath];
    }];
}

- (void)doraemon_showEdgeInsetsAndAutomicSetWithKeyPath:(NSString *)keyPath {
    __block NSValue *value = [self valueForKeyPath:keyPath];
    __weak typeof(self) weakSelf = self;
    [self doraemon_showTextFieldAlertWithText:NSStringFromUIEdgeInsets([value UIEdgeInsetsValue]) handler:^(NSString * _Nullable newText) {
        [weakSelf setValue:[NSValue valueWithUIEdgeInsets:[weakSelf doraemon_insetsFromString:newText originalInsets:[value UIEdgeInsetsValue]]] forKeyPath:keyPath];
    }];
}

- (void)doraemon_showTextAlertAndAutomicSetWithKeyPath:(NSString *)keyPath {
    __weak typeof(self) weakSelf = self;
    [self doraemon_showTextFieldAlertWithText:[self valueForKeyPath:keyPath] handler:^(NSString * _Nullable newText) {
        [weakSelf setValue:newText forKeyPath:keyPath];
    }];
}

- (void)doraemon_showAttributeTextAlertAndAutomicSetWithKeyPath:(NSString *)keyPath {
    __block NSAttributedString *attribute = [self valueForKeyPath:keyPath];
    __weak typeof(self) weakSelf = self;
    [self doraemon_showTextFieldAlertWithText:attribute.string handler:^(NSString * _Nullable newText) {
        NSMutableAttributedString *mutAttribute = [[NSMutableAttributedString alloc] initWithAttributedString:attribute];
        [mutAttribute replaceCharactersInRange:NSMakeRange(0, attribute.string.length) withString:newText];
        [weakSelf setValue:[mutAttribute copy] forKeyPath:keyPath];
    }];
}

- (void)doraemon_showSizeAlertAndAutomicSetWithKeyPath:(NSString *)keyPath {
    __block NSValue *value = [self valueForKeyPath:keyPath];
    __weak typeof(self) weakSelf = self;
    [self doraemon_showTextFieldAlertWithText:NSStringFromCGSize([value CGSizeValue]) handler:^(NSString * _Nullable newText) {
        [weakSelf setValue:[NSValue valueWithCGSize:[weakSelf doraemon_sizeFromString:newText originalSize:[value CGSizeValue]]] forKeyPath:keyPath];
    }];
}

- (void)doraemon_showDateAlertAndAutomicSetWithKeyPath:(NSString *)keyPath {
    NSDate *date = [self valueForKeyPath:keyPath];
    __weak typeof(self) weakSelf = self;
    [self doraemon_showTextFieldAlertWithText:[DoraemonHierarchyFormatterTool stringFromDate:date] handler:^(NSString * _Nullable newText) {
        NSDate *newDate = [DoraemonHierarchyFormatterTool dateFromString:newText];
        if (newDate) {
            [weakSelf setValue:newDate forKeyPath:keyPath];
        }
    }];
}

- (void)doraemon_showTextFieldAlertWithText:(NSString *)text handler:(nullable void (^)(NSString * _Nullable newText))handler {
    __weak typeof(self) weakSelf = self;
    UIWindow *window = (UIWindow *)[DoraemonHierarchyHelper shared].window;
    [window.rootViewController.doraemon_currentShowingViewController doraemon_showTextFieldAlertControllerWithMessage:@"Change Property" text:text handler:^(NSString * _Nullable newText) {
        if (handler) {
            handler(newText);
        }
        [weakSelf doraemon_postHierarchyChangeNotification];
    }];
}

- (void)doraemon_showActionSheetWithActions:(NSArray *)actions currentAction:(NSString *)currentAction completion:(void (^)(NSInteger index))completion {
    __weak typeof(self) weakSelf = self;
    UIWindow *window = (UIWindow *)[DoraemonHierarchyHelper shared].window;
    [window.rootViewController.doraemon_currentShowingViewController doraemon_showActionSheetWithTitle:@"Change Property" actions:actions currentAction:currentAction completion:^(NSInteger index) {
        if (completion) {
            completion(index);
        }
        [weakSelf doraemon_postHierarchyChangeNotification];
    }];
}

- (void)doraemon_postHierarchyChangeNotification {
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonHierarchyChangeNotificationName object:self];
}

- (void)doraemon_replaceAttributeString:(NSString *)newString key:(NSString *)key {
    NSAttributedString *string = [self valueForKey:key];
    if (string && ![string isKindOfClass:[NSAttributedString class]]) {
        return;
    }
    NSMutableAttributedString *attribute = string ? [[NSMutableAttributedString alloc] initWithAttributedString:string] : [[NSMutableAttributedString alloc] init];
    [attribute replaceCharactersInRange:NSMakeRange(0, string.length) withString:newString];
    [self setValue:string forKey:key];
}

@end

@implementation UIView (DoraemonHierarchy)

#pragma mark - Public
- (NSArray <DoraemonHierarchyCategoryModel *>*)doraemon_sizeHierarchyCategoryModels {
    __weak typeof(self) weakSelf = self;
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Frame" detailTitle:[self doraemon_hierarchyPointDescription:self.frame.origin]] noneInsets];
    model1.block = ^{
        [weakSelf doraemon_showFrameAlertAndAutomicSetWithKeyPath:@"frame"];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[self doraemon_hierarchySizeDescription:self.frame.size]] noneInsets];
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Bounds" detailTitle:[self doraemon_hierarchyPointDescription:self.bounds.origin]] noneInsets];
    model3.block = ^{
        [weakSelf doraemon_showFrameAlertAndAutomicSetWithKeyPath:@"bounds"];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[self doraemon_hierarchySizeDescription:self.bounds.size]] noneInsets];
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Center" detailTitle:[self doraemon_hierarchyPointDescription:self.center]] noneInsets];
    model5.block = ^{
        [weakSelf doraemon_showPointAlertAndAutomicSetWithKeyPath:@"center"];
    };
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Position" detailTitle:[self doraemon_hierarchyPointDescription:self.layer.position]] noneInsets];
    model6.block = ^{
        [weakSelf doraemon_showPointAlertAndAutomicSetWithKeyPath:@"layer.position"];
    };
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Z Position" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.layer.zPosition)]];
    model7.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"layer.zPosition"];
    };
    [settings addObject:model7];
    
    DoraemonHierarchyCellModel *model8 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Anchor Point" detailTitle:[self doraemon_hierarchyPointDescription:self.layer.anchorPoint]] noneInsets];
    model8.block = ^{
        [weakSelf doraemon_showPointAlertAndAutomicSetWithKeyPath:@"layer.anchorPoint"];
    };
    [settings addObject:model8];
    
    DoraemonHierarchyCellModel *model9 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Anchor Point Z" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.layer.anchorPointZ)]];
    model9.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"layer.anchorPointZ"];
    };
    [settings addObject:model9];

    DoraemonHierarchyCellModel *lastConstrainModel = nil;
    
    for (NSLayoutConstraint *constrain in self.constraints) {
        if (!constrain.shouldBeArchived) {
            continue;
        }
        NSString *constrainDesc = [self doraemon_hierarchyLayoutConstraintDescription:constrain];
        if (constrainDesc) {
            DoraemonHierarchyCellModel *mod = [[[DoraemonHierarchyCellModel alloc] initWithTitle:lastConstrainModel ? nil : @"Constrains" detailTitle:constrainDesc] noneInsets];
            __weak NSLayoutConstraint *cons = constrain;
            mod.block = ^{
                [weakSelf doraemon_showTextFieldAlertWithText:[DoraemonHierarchyFormatterTool formatNumber:@(cons.constant)] handler:^(NSString * _Nullable newText) {
                    cons.constant = [newText doubleValue];
                    [weakSelf setNeedsLayout];
                }];
            };
            [settings addObject:mod];
            lastConstrainModel = mod;
        }
    }
    
    for (NSLayoutConstraint *constrain in self.superview.constraints) {
        if (!constrain.shouldBeArchived) {
            continue;
        }
        if (constrain.firstItem == self || constrain.secondItem == self) {
            NSString *constrainDesc = [self doraemon_hierarchyLayoutConstraintDescription:constrain];
            if (constrainDesc) {
                DoraemonHierarchyCellModel *mod = [[[DoraemonHierarchyCellModel alloc] initWithTitle:lastConstrainModel ? nil : @"Constrains" detailTitle:constrainDesc] noneInsets];
                __weak NSLayoutConstraint *cons = constrain;
                mod.block = ^{
                    [weakSelf doraemon_showTextFieldAlertWithText:[DoraemonHierarchyFormatterTool formatNumber:@(cons.constant)] handler:^(NSString * _Nullable newText) {
                        cons.constant = [newText doubleValue];
                        [weakSelf setNeedsLayout];
                    }];
                };
                [settings addObject:mod];
                lastConstrainModel = mod;
            }
        }
    }
    
    [lastConstrainModel normalInsets];
    
    return @[[[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"View" items:settings]];
}

#pragma mark - Primary
- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    
    __weak typeof(self) weakSelf = self;
    
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Layer" detailTitle:self.layer.description] noneInsets];
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Layer Class" detailTitle:NSStringFromClass(self.layer.class)];
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Content Model" detailTitle:[DoraemonEnumDescription viewContentModeDescription:self.contentMode]] noneInsets];
    model3.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription viewContentModeDescriptions] currentAction:[DoraemonEnumDescription viewContentModeDescription:weakSelf.contentMode] completion:^(NSInteger index) {
            weakSelf.contentMode = index;
        }];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Tag" detailTitle:[NSString stringWithFormat:@"%ld",(long)self.tag]];
    model4.block = ^{
        [weakSelf doraemon_showIntAlertAndAutomicSetWithKeyPath:@"tag"];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"User Interaction" flag: self.isUserInteractionEnabled] noneInsets];
    model5.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.userInteractionEnabled = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Multiple Touch" flag:self.isMultipleTouchEnabled];
    model6.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.multipleTouchEnabled = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Alpha" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.alpha)]] noneInsets];
    model7.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"alpha"];
    };
    [settings addObject:model7];
    
    DoraemonHierarchyCellModel *model8 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Background" detailTitle:[self doraemon_hierarchyColorDescription:self.backgroundColor]] noneInsets];
    model8.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"backgroundColor"];
    };
    [settings addObject:model8];
    
    DoraemonHierarchyCellModel *model9 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Tint" detailTitle:[self doraemon_hierarchyColorDescription:self.tintColor]];
    model9.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"tintColor"];
    };
    [settings addObject:model9];
    
    DoraemonHierarchyCellModel *model10 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Drawing" detailTitle:@"Opaque" flag:self.isOpaque] noneInsets];
    model10.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.opaque = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model10];
    
    DoraemonHierarchyCellModel *model11 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Hidden" flag:self.isHidden] noneInsets];
    model11.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.hidden = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model11];
    
    DoraemonHierarchyCellModel *model12 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Clears Graphics Context" flag:self.clearsContextBeforeDrawing] noneInsets];
    model12.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.clearsContextBeforeDrawing = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model12];
    
    DoraemonHierarchyCellModel *model13 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Clip To Bounds" flag:self.clipsToBounds] noneInsets];
    model13.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.clipsToBounds = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model13];
    
    DoraemonHierarchyCellModel *model14 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Autoresizes Subviews" flag:self.autoresizesSubviews];
    model14.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.autoresizesSubviews = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model14];
    
    DoraemonHierarchyCellModel *model15 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Trait Collection" detailTitle:nil] noneInsets];
    [settings addObject:model15];
    
    if (@available(iOS 12.0, *)) {
        DoraemonHierarchyCellModel *model16 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[DoraemonEnumDescription userInterfaceStyleDescription:self.traitCollection.userInterfaceStyle]] noneInsets];
        [settings addObject:model16];
    }
    
    DoraemonHierarchyCellModel *model17 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[@"Vertical" stringByAppendingFormat:@" %@",[DoraemonEnumDescription userInterfaceSizeClassDescription:self.traitCollection.verticalSizeClass]]] noneInsets];
    [settings addObject:model17];
    
    DoraemonHierarchyCellModel *model18 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[@"Horizontal" stringByAppendingFormat:@" %@",[DoraemonEnumDescription userInterfaceSizeClassDescription:self.traitCollection.horizontalSizeClass]]] noneInsets];
    [settings addObject:model18];
    
    if (@available(iOS 10.0, *)) {
        DoraemonHierarchyCellModel *model19 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[DoraemonEnumDescription traitEnvironmentLayoutDirectionDescription:self.traitCollection.layoutDirection]];
        [settings addObject:model19];
    }
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"View" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

- (NSString *)doraemon_hierarchyLayoutConstraintDescription:(NSLayoutConstraint *)constraint {
    NSMutableString *string = [[NSMutableString alloc] init];
    if (constraint.firstItem == self) {
        [string appendString:@"self."];
    } else if (constraint.firstItem == self.superview) {
        [string appendString:@"superview."];
    } else {
        [string appendFormat:@"%@.",NSStringFromClass([constraint.firstItem class])];
    }
    [string appendString:[DoraemonEnumDescription layoutAttributeDescription:constraint.firstAttribute]];
    [string appendString:[DoraemonEnumDescription layoutRelationDescription:constraint.relation]];
    if (constraint.secondItem) {
        if (constraint.secondItem == self) {
            [string appendString:@"self."];
        } else if (constraint.secondItem == self.superview) {
            [string appendString:@"superview."];
        } else {
            [string appendFormat:@"%@.",NSStringFromClass([constraint.secondItem class])];
        }
        [string appendString:[DoraemonEnumDescription layoutAttributeDescription:constraint.secondAttribute]];
        if (constraint.multiplier != 1) {
            [string appendFormat:@" * %@",[DoraemonHierarchyFormatterTool formatNumber:@(constraint.multiplier)]];
        }
        if (constraint.constant > 0) {
            [string appendFormat:@" + %@",[DoraemonHierarchyFormatterTool formatNumber:@(constraint.constant)]];
        } else if (constraint.constant < 0) {
            [string appendFormat:@" - %@",[DoraemonHierarchyFormatterTool formatNumber:@(fabs(constraint.constant))]];
        }
    } else if (constraint.constant) {
        [string appendString:[DoraemonHierarchyFormatterTool formatNumber:@(constraint.constant)]];
    } else {
        return nil;
    }
    
    [string appendFormat:@" @ %@",[DoraemonHierarchyFormatterTool formatNumber:@(constraint.priority)]];
    return string;
}

@end

@implementation UILabel (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self) weakSelf = self;
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Text" detailTitle:[self doraemon_hierarchyTextDescription:self.text]] noneInsets];
    model1.block = ^{
        [weakSelf doraemon_showTextAlertAndAutomicSetWithKeyPath:@"text"];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:self.attributedText == nil ? @"Plain Text" : @"Attributed Text"] noneInsets];
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Text" detailTitle:[self doraemon_hierarchyColorDescription:self.textColor]] noneInsets];
    model3.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"textColor"];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[self doraemon_hierarchyObjectDescription:self.font]] noneInsets];
    model4.block = ^{
        [weakSelf doraemon_showFontAlertAndAutomicSetWithKeyPath:@"font"];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[NSString stringWithFormat:@"Aligned %@", [DoraemonEnumDescription textAlignmentDescription:self.textAlignment]]] noneInsets];
    model5.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription textAlignments] currentAction:[DoraemonEnumDescription textAlignmentDescription:weakSelf.textAlignment] completion:^(NSInteger index) {
            weakSelf.textAlignment = index;
        }];
    };
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Lines" detailTitle:[NSString stringWithFormat:@"%ld",(long)self.numberOfLines]] noneInsets];
    model6.block = ^{
        [weakSelf doraemon_showIntAlertAndAutomicSetWithKeyPath:@"numberOfLines"];
    };
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Behavior" detailTitle:@"Enabled" flag:self.isEnabled] noneInsets];
    model7.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.enabled = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model7];
    
    DoraemonHierarchyCellModel *model8 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Highlighted" flag:self.isHighlighted];
    model8.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.highlighted = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model8];
    
    DoraemonHierarchyCellModel *model9 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Baseline" detailTitle:[NSString stringWithFormat:@"Align %@",[DoraemonEnumDescription baselineAdjustmentDescription:self.baselineAdjustment]]] noneInsets];
    model9.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription baselineAdjustments] currentAction:[DoraemonEnumDescription baselineAdjustmentDescription:weakSelf.baselineAdjustment] completion:^(NSInteger index) {
            weakSelf.baselineAdjustment = index;
        }];
    };
    [settings addObject:model9];
    
    DoraemonHierarchyCellModel *model10 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Line Break" detailTitle:[DoraemonEnumDescription lineBreakModeDescription:self.lineBreakMode]] noneInsets];
    model10.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription lineBreaks] currentAction:[DoraemonEnumDescription lineBreakModeDescription:weakSelf.lineBreakMode] completion:^(NSInteger index) {
            weakSelf.lineBreakMode = index;
        }];
    };
    [settings addObject:model10];
    
    DoraemonHierarchyCellModel *model11 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Min Font Scale" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.minimumScaleFactor)]];
    model11.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"minimumScaleFactor"];
    };
    [settings addObject:model11];
    
    DoraemonHierarchyCellModel *model12 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Highlighted" detailTitle:[self doraemon_hierarchyColorDescription:self.highlightedTextColor]] noneInsets];
    model12.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"highlightedTextColor"];
    };
    [settings addObject:model12];
    
    DoraemonHierarchyCellModel *model13 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Shadow" detailTitle:[self doraemon_hierarchyColorDescription:self.shadowColor]] noneInsets];
    model13.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"shadowColor"];
    };
    [settings addObject:model13];
    
    DoraemonHierarchyCellModel *model14 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Shadow Offset" detailTitle:[self doraemon_hierarchySizeDescription:self.shadowOffset]];
    model14.block = ^{
        [weakSelf doraemon_showSizeAlertAndAutomicSetWithKeyPath:@"shadowOffset"];
    };
    [settings addObject:model14];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Label" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UIControl (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self) weakSelf = self;
    
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Alignment" detailTitle:[NSString stringWithFormat:@"%@ Horizonally", [DoraemonEnumDescription controlContentHorizontalAlignmentDescription:self.contentHorizontalAlignment]]] noneInsets];
    model1.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription controlContentHorizontalAlignments] currentAction:[DoraemonEnumDescription controlContentHorizontalAlignmentDescription:weakSelf.contentHorizontalAlignment] completion:^(NSInteger index) {
            weakSelf.contentHorizontalAlignment = index;
        }];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[NSString stringWithFormat:@"%@ Vertically", [DoraemonEnumDescription controlContentVerticalAlignmentDescription:self.contentVerticalAlignment]]];
    model2.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription controlContentVerticalAlignments] currentAction:[DoraemonEnumDescription controlContentVerticalAlignmentDescription:weakSelf.contentVerticalAlignment] completion:^(NSInteger index) {
            weakSelf.contentVerticalAlignment = index;
        }];
    };
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Content" detailTitle:self.isSelected ? @"Selected" : @"Not Selected" flag:self.isSelected] noneInsets];
    model3.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.selected = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:self.isEnabled ? @"Enabled" : @"Not Enabled" flag:self.isEnabled] noneInsets];
    model4.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.enabled = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:self.isHighlighted ? @"Highlighted" : @"Not Highlighted" flag:self.isHighlighted];
    model5.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.highlighted = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model5];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Control" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UIButton (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    
    __weak typeof(self) weakSelf = self;
    
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Type" detailTitle:[DoraemonEnumDescription buttonTypeDescription:self.buttonType]];
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"State" detailTitle:[DoraemonEnumDescription controlStateDescription:self.state]] noneInsets];
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Title" detailTitle:[self doraemon_hierarchyTextDescription:self.currentTitle]] noneInsets];
    model3.block = ^{
        [weakSelf doraemon_showTextFieldAlertWithText:weakSelf.currentTitle handler:^(NSString * _Nullable newText) {
            [weakSelf setTitle:newText forState:weakSelf.state];
        }];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:self.currentAttributedTitle == nil ? @"Plain Text" : @"Attributed Text"] noneInsets];
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Text Color" detailTitle:[self doraemon_hierarchyColorDescription:self.currentTitleColor]] noneInsets];
    model5.block = ^{
        [weakSelf doraemon_showTextFieldAlertWithText:[weakSelf.currentTitleColor doraemon_HexString] handler:^(NSString * _Nullable newText) {
            [weakSelf setTitleColor:[weakSelf doraemon_colorFromString:newText originalColor:weakSelf.currentTitleColor] forState:weakSelf.state];
        }];
    };
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Shadow Color" detailTitle:[self doraemon_hierarchyColorDescription:self.currentTitleShadowColor]];
    model6.block = ^{
        [weakSelf doraemon_showTextFieldAlertWithText:[weakSelf.currentTitleShadowColor doraemon_HexString] handler:^(NSString * _Nullable newText) {
            [weakSelf setTitleShadowColor:[weakSelf doraemon_colorFromString:newText originalColor:weakSelf.currentTitleShadowColor] forState:weakSelf.state];
        }];
    };
    [settings addObject:model6];
    
    id target = self.allTargets.allObjects.firstObject;
    DoraemonHierarchyCellModel *model7 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Target" detailTitle:target ? [NSString stringWithFormat:@"%@",target] : @"<nil>"] noneInsets];
    [settings addObject:model7];
    
    DoraemonHierarchyCellModel *model8 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Action" detailTitle:[self doraemon_hierarchyTextDescription:[self actionsForTarget:target forControlEvent:UIControlEventTouchUpInside].firstObject]];;
    [settings addObject:model8];
    
    DoraemonHierarchyCellModel *model9 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Image" detailTitle:[self doraemon_hierarchyImageDescription:self.currentImage]];
    [settings addObject:model9];
    
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    DoraemonHierarchyCellModel *model10 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Shadow Offset" detailTitle:[self doraemon_hierarchySizeDescription:self.titleShadowOffset]] noneInsets];
    model10.block = ^{
        [weakSelf doraemon_showSizeAlertAndAutomicSetWithKeyPath:@"titleShadowOffset"];
    };
    [settings addObject:model10];
#pragma clang diagnostic pop
    
    DoraemonHierarchyCellModel *model11 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"On Highlight" detailTitle:self.reversesTitleShadowWhenHighlighted ? @"Shadow Reverses" : @"Normal Shadow"] noneInsets];
    [settings addObject:model11];
    
    DoraemonHierarchyCellModel *model12 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:self.showsTouchWhenHighlighted ? @"Shows Touch" : @"Doesn't Show Touch"] noneInsets];
    [settings addObject:model12];
    
    DoraemonHierarchyCellModel *model13 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:self.adjustsImageWhenHighlighted ? @"Adjusts Image" : @"No Image Adjustment"] noneInsets];
    [settings addObject:model13];
    
    DoraemonHierarchyCellModel *model14 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"When Disabled" detailTitle:self.adjustsImageWhenDisabled ? @"Adjusts Image" : @"No Image Adjustment"] noneInsets];
    [settings addObject:model14];
    
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    DoraemonHierarchyCellModel *model15 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Line Break" detailTitle:[DoraemonEnumDescription lineBreakModeDescription:self.lineBreakMode]];
    [settings addObject:model15];
#pragma clang diagnostic pop
    
    DoraemonHierarchyCellModel *model16 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Content Insets" detailTitle:[self doraemon_hierarchyInsetsTopBottomDescription:self.contentEdgeInsets]] noneInsets];
    model16.block = ^{
        [weakSelf doraemon_showEdgeInsetsAndAutomicSetWithKeyPath:@"contentEdgeInsets"];
    };
    [settings addObject:model16];
    
    DoraemonHierarchyCellModel *model17 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[self doraemon_hierarchyInsetsLeftRightDescription:self.contentEdgeInsets]] noneInsets];
    [settings addObject:model17];
    
    DoraemonHierarchyCellModel *model18 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Title Insets" detailTitle:[self doraemon_hierarchyInsetsTopBottomDescription:self.titleEdgeInsets]] noneInsets];
    model18.block = ^{
        [weakSelf doraemon_showEdgeInsetsAndAutomicSetWithKeyPath:@"titleEdgeInsets"];
    };
    [settings addObject:model18];
    
    DoraemonHierarchyCellModel *model19 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[self doraemon_hierarchyInsetsLeftRightDescription:self.titleEdgeInsets]] noneInsets];
    [settings addObject:model19];
    
    DoraemonHierarchyCellModel *model20 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Image Insets" detailTitle:[self doraemon_hierarchyInsetsTopBottomDescription:self.imageEdgeInsets]] noneInsets];
    model20.block = ^{
        [weakSelf doraemon_showEdgeInsetsAndAutomicSetWithKeyPath:@"imageEdgeInsets"];
    };
    [settings addObject:model20];
    
    DoraemonHierarchyCellModel *model21 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[self doraemon_hierarchyInsetsLeftRightDescription:self.imageEdgeInsets]];
    [settings addObject:model21];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Button" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UIImageView (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self)weakSelf = self;
    
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Image" detailTitle: [self doraemon_hierarchyImageDescription:self.image]];
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Highlighted" detailTitle: [self doraemon_hierarchyImageDescription:self.highlightedImage]];
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"State" detailTitle:self.isHighlighted ? @"Highlighted" : @"Not Highlighted" flag:self.isHighlighted];
    model3.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.highlighted = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Image View" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UITextField (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self) weakSelf = self;
    
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Plain Text" detailTitle:[self doraemon_hierarchyTextDescription:self.text]] noneInsets];
    model1.block = ^{
        [weakSelf doraemon_showTextAlertAndAutomicSetWithKeyPath:@"text"];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Attributed Text" detailTitle:[self doraemon_hierarchyObjectDescription:self.attributedText]] noneInsets];
    model2.block = ^{
        [weakSelf doraemon_showAttributeTextAlertAndAutomicSetWithKeyPath:@"attributedText"];
    };
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Allows Editing Attributes" flag:self.allowsEditingTextAttributes] noneInsets];
    model3.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.allowsEditingTextAttributes = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Color" detailTitle:[self doraemon_hierarchyColorDescription:self.textColor]] noneInsets];
    model4.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"textColor"];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Font" detailTitle:[self doraemon_hierarchyObjectDescription:self.font]] noneInsets];
    model5.block = ^{
        [weakSelf doraemon_showFontAlertAndAutomicSetWithKeyPath:@"font"];
    };
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Alignment" detailTitle:[DoraemonEnumDescription textAlignmentDescription:self.textAlignment]] noneInsets];
    model6.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription textAlignments] currentAction:[DoraemonEnumDescription textAlignmentDescription:weakSelf.textAlignment] completion:^(NSInteger index) {
            weakSelf.textAlignment = index;
        }];
    };
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Placeholder" detailTitle:[self doraemon_hierarchyTextDescription:self.placeholder ?: self.attributedPlaceholder.string]];
    model7.block = ^{
        if (weakSelf.placeholder) {
            [weakSelf doraemon_showTextAlertAndAutomicSetWithKeyPath:@"placeholder"];
        } else {
            [weakSelf doraemon_showAttributeTextAlertAndAutomicSetWithKeyPath:@"attributedPlaceholder"];
        }
    };
    [settings addObject:model7];
    
    DoraemonHierarchyCellModel *model8 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Background" detailTitle: [self doraemon_hierarchyImageDescription:self.background]] noneInsets];
    [settings addObject:model8];
    
    DoraemonHierarchyCellModel *model9 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Disabled" detailTitle: [self doraemon_hierarchyImageDescription:self.disabledBackground]];
    [settings addObject:model9];
    
    DoraemonHierarchyCellModel *model10 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Border Style" detailTitle:[DoraemonEnumDescription textBorderStyleDescription:self.borderStyle]];
    model10.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription textBorderStyles] currentAction:[DoraemonEnumDescription textBorderStyleDescription:weakSelf.borderStyle] completion:^(NSInteger index) {
            weakSelf.borderStyle = index;
        }];
    };
    [settings addObject:model10];
    
    DoraemonHierarchyCellModel *model11 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Clear Button" detailTitle:[DoraemonEnumDescription textFieldViewModeDescription:self.clearButtonMode]] noneInsets];
    model11.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription textFieldViewModes] currentAction:[DoraemonEnumDescription textFieldViewModeDescription:weakSelf.clearButtonMode] completion:^(NSInteger index) {
            weakSelf.clearButtonMode = index;
        }];
    };
    [settings addObject:model11];
    
    DoraemonHierarchyCellModel *model12 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Clear when editing begins" flag:self.clearsOnBeginEditing];
    model12.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.clearsOnBeginEditing = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model12];
    
    DoraemonHierarchyCellModel *model13 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Min Font Size" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.minimumFontSize)]] noneInsets];
    model13.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"minimumFontSize"];
    };
    [settings addObject:model13];
    
    DoraemonHierarchyCellModel *model14 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Adjusts to Fit" flag:self.adjustsFontSizeToFitWidth];
    model14.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.adjustsFontSizeToFitWidth = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model14];
    
    DoraemonHierarchyCellModel *model15 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Capitalization" detailTitle:[DoraemonEnumDescription textAutocapitalizationTypeDescription:self.autocapitalizationType]] noneInsets];
    model15.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription textAutocapitalizationTypes] currentAction:[DoraemonEnumDescription textAutocapitalizationTypeDescription:weakSelf.autocapitalizationType] completion:^(NSInteger index) {
            weakSelf.autocapitalizationType = index;
        }];
    };
    [settings addObject:model15];
    
    DoraemonHierarchyCellModel *model16 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Correction" detailTitle:[DoraemonEnumDescription textAutocorrectionTypeDescription:self.autocorrectionType]] noneInsets];
    model16.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription textAutocorrectionTypes] currentAction:[DoraemonEnumDescription textAutocorrectionTypeDescription:weakSelf.autocorrectionType] completion:^(NSInteger index) {
            weakSelf.autocorrectionType = index;
        }];
    };
    [settings addObject:model16];
    
    DoraemonHierarchyCellModel *model17 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Keyboard" detailTitle:[DoraemonEnumDescription keyboardTypeDescription:self.keyboardType]] noneInsets];
    model17.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription keyboardTypes] currentAction:[DoraemonEnumDescription keyboardTypeDescription:weakSelf.keyboardType] completion:^(NSInteger index) {
            weakSelf.keyboardType = index;
        }];
    };
    [settings addObject:model17];
    
    DoraemonHierarchyCellModel *model18 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Appearance" detailTitle:[DoraemonEnumDescription keyboardAppearanceDescription:self.keyboardAppearance]] noneInsets];
    model18.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription keyboardAppearances] currentAction:[DoraemonEnumDescription keyboardAppearanceDescription:weakSelf.keyboardAppearance] completion:^(NSInteger index) {
            weakSelf.keyboardAppearance = index;
        }];
    };
    [settings addObject:model18];
    
    DoraemonHierarchyCellModel *model19 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Return Key" detailTitle:[DoraemonEnumDescription returnKeyTypeDescription:self.returnKeyType]] noneInsets];
    model19.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription returnKeyTypes] currentAction:[DoraemonEnumDescription returnKeyTypeDescription:weakSelf.returnKeyType] completion:^(NSInteger index) {
            weakSelf.returnKeyType = index;
        }];
    };
    [settings addObject:model19];
    
    DoraemonHierarchyCellModel *model20 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Auto-enable Return Key" flag:self.enablesReturnKeyAutomatically] noneInsets];
    model20.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.enablesReturnKeyAutomatically = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model20];
    
    DoraemonHierarchyCellModel *model21 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Secure Entry" flag:self.isSecureTextEntry];
    model21.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.secureTextEntry = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model21];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Text Field" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UISegmentedControl (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self) weakSelf = self;
    
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Behavior" detailTitle:self.isMomentary ? @"Momentary" : @"Persistent Selection" flag:self.isMomentary] noneInsets];
    model1.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.momentary = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Segments" detailTitle:[NSString stringWithFormat:@"%ld",(unsigned long)self.numberOfSegments]];
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Selected Index" detailTitle:[NSString stringWithFormat:@"%ld",(long)self.selectedSegmentIndex]] noneInsets];
    model3.block = ^{
        NSMutableArray *actions = [[NSMutableArray alloc] init];
        for (NSInteger i = 0; i < weakSelf.numberOfSegments; i++) {
            [actions addObject:[NSString stringWithFormat:@"%ld",(long)i]];
        }
        [weakSelf doraemon_showActionSheetWithActions:actions currentAction:[NSString stringWithFormat:@"%ld",(long)weakSelf.selectedSegmentIndex] completion:^(NSInteger index) {
            weakSelf.selectedSegmentIndex = index;
        }];
    };
    [settings addObject:model3];
    
#ifdef __IPHONE_13_0
    if (@available(iOS 13.0, *)) {
        DoraemonHierarchyCellModel *model4 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Large title" detailTitle:[self doraemon_hierarchyTextDescription:self.largeContentTitle]] noneInsets];
        model4.block = ^{
            [weakSelf doraemon_showTextAlertAndAutomicSetWithKeyPath:@"largeContentTitle"];
        };
        [settings addObject:model4];
        
        DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Image" detailTitle: [self doraemon_hierarchyImageDescription:self.largeContentImage]] noneInsets];
        [settings addObject:model5];
    }
#endif
    
    DoraemonHierarchyCellModel *model6 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Selected" detailTitle:[self isEnabledForSegmentAtIndex:self.selectedSegmentIndex] ? @"Enabled" : @"Not Enabled"];
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Offset" detailTitle:[self doraemon_hierarchySizeDescription:[self contentOffsetForSegmentAtIndex:self.selectedSegmentIndex]]] noneInsets];
    [settings addObject:model7];
    
    DoraemonHierarchyCellModel *model8 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Size Mode" detailTitle:self.apportionsSegmentWidthsByContent ? @"Proportional to Content" : @"Equal Widths" flag:self.apportionsSegmentWidthsByContent] noneInsets];
    model8.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.apportionsSegmentWidthsByContent = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model8];
    
    DoraemonHierarchyCellModel *model9 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Width" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@([self widthForSegmentAtIndex:self.selectedSegmentIndex])]];
    [settings addObject:model9];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Segmented Control" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UISlider (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self)weakSelf = self;
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Current" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.value)]] noneInsets];
    model1.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"value"];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Minimum" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.minimumValue)]] noneInsets];
    model2.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"minimumValue"];
    };
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Maximum" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.maximumValue)]];
    model3.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"maximumValue"];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Min Image" detailTitle: [self doraemon_hierarchyImageDescription:self.minimumValueImage]] noneInsets];
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Max Image" detailTitle: [self doraemon_hierarchyImageDescription:self.maximumValueImage]];
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Min Track Tint" detailTitle:[self doraemon_hierarchyColorDescription:self.minimumTrackTintColor]] noneInsets];
    model6.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"minimumTrackTintColor"];
    };
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Max Track Tint" detailTitle:[self doraemon_hierarchyColorDescription:self.maximumTrackTintColor]] noneInsets];
    model7.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"maximumTrackTintColor"];
    };
    [settings addObject:model7];
    
    DoraemonHierarchyCellModel *model8 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Thumb Tint" detailTitle:[self doraemon_hierarchyColorDescription:self.tintColor]];
    model8.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"tintColor"];
    };
    [settings addObject:model8];
    
    DoraemonHierarchyCellModel *model9 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Events" detailTitle:@"Continuous Update" flag:self.isContinuous];
    model9.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.continuous = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model9];
    
    DoraemonHierarchyCategoryModel *model =  [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Slider" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UISwitch (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self)weakSelf = self;
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"State" flag:self.isOn] noneInsets];
    model1.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.on = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"On Tint" detailTitle:[self doraemon_hierarchyColorDescription:self.onTintColor]] noneInsets];
    model2.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"onTintColor"];
    };
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Thumb Tint" detailTitle:[self doraemon_hierarchyColorDescription:self.thumbTintColor]];
    model3.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"thumbTintColor"];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Switch" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UIActivityIndicatorView (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self)weakSelf = self;
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Style" detailTitle:[DoraemonEnumDescription activityIndicatorViewStyleDescription:self.activityIndicatorViewStyle]] noneInsets];
    model1.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription activityIndicatorViewStyles] currentAction:[DoraemonEnumDescription activityIndicatorViewStyleDescription:weakSelf.activityIndicatorViewStyle] completion:^(NSInteger index) {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
            if (index <= UIActivityIndicatorViewStyleGray) {
                weakSelf.activityIndicatorViewStyle = index;
            } else {
                #if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
                if (@available(iOS 13.0, *)) {
                    weakSelf.activityIndicatorViewStyle = index + (UIActivityIndicatorViewStyleMedium - UIActivityIndicatorViewStyleGray - 1);
                }
                #endif
            }
#pragma clang diagnostic pop
        }];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Color" detailTitle:[self doraemon_hierarchyColorDescription:self.color]] noneInsets];
    model2.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"color"];
    };
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Behavior" detailTitle:@"Animating" flag:self.isAnimating] noneInsets];
    model3.changePropertyBlock = ^(id  _Nullable obj) {
        if ([obj boolValue]) {
            if (!weakSelf.isAnimating) {
                [weakSelf startAnimating];
            };
        } else {
            if (weakSelf.isAnimating) {
                [weakSelf stopAnimating];
            }
        }
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Hides When Stopped" flag:self.hidesWhenStopped];
    model4.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.hidesWhenStopped = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Activity Indicator View" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UIProgressView (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self)weakSelf = self;
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Style" detailTitle:[DoraemonEnumDescription progressViewStyleDescription:self.progressViewStyle]];
    model1.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription progressViewStyles] currentAction:[DoraemonEnumDescription progressViewStyleDescription:weakSelf.progressViewStyle] completion:^(NSInteger index) {
            weakSelf.progressViewStyle = index;
        }];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Progress" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.progress)]];
    model2.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"progress"];
    };
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Progress Tint" detailTitle:[self doraemon_hierarchyColorDescription:self.progressTintColor]] noneInsets];
    model3.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"progressTintColor"];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Track Tint" detailTitle:[self doraemon_hierarchyColorDescription:self.trackTintColor]];
    model4.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"trackTintColor"];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Progress Image" detailTitle:[self doraemon_hierarchyImageDescription:self.progressImage]] noneInsets];
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Track Image" detailTitle:[self doraemon_hierarchyImageDescription:self.trackImage]];
    [settings addObject:model6];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Progress View" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UIPageControl (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self)weakSelf = self;
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Pages" detailTitle:[NSString stringWithFormat:@"%ld",(long)self.numberOfPages]] noneInsets];
    model1.block = ^{
        [weakSelf doraemon_showIntAlertAndAutomicSetWithKeyPath:@"numberOfPages"];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Current Page" detailTitle:[NSString stringWithFormat:@"%ld",(long)self.currentPage]] noneInsets];
    model2.block = ^{
        if (weakSelf.numberOfPages < 10) {
            NSMutableArray *actions = [[NSMutableArray alloc] init];
            for (NSInteger i = 0; i < weakSelf.numberOfPages; i++) {
                [actions addObject:[NSString stringWithFormat:@"%ld",(long)i]];
            }
            [weakSelf doraemon_showActionSheetWithActions:actions currentAction:[NSString stringWithFormat:@"%ld",(long)weakSelf.currentPage] completion:^(NSInteger index) {
                weakSelf.currentPage = index;
            }];
        } else {
            [weakSelf doraemon_showIntAlertAndAutomicSetWithKeyPath:@"currentPage"];
        }
    };
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Behavior" detailTitle:@"Hides for Single Page" flag:self.hidesForSinglePage] noneInsets];
    model3.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.hidesForSinglePage = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Defers Page Display" flag:self.defersCurrentPageDisplay];
    model4.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.defersCurrentPageDisplay = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Tint Color" detailTitle:[self doraemon_hierarchyColorDescription:self.pageIndicatorTintColor]] noneInsets];
    model5.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"pageIndicatorTintColor"];
    };
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Current Page" detailTitle:[self doraemon_hierarchyColorDescription:self.currentPageIndicatorTintColor]];
    model6.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"currentPageIndicatorTintColor"];
    };
    [settings addObject:model6];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Page Control" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UIStepper (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    
    __weak typeof(self)weakSelf = self;
    
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Value" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.value)]] noneInsets];
    model1.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"value"];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Minimum" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.minimumValue)]] noneInsets];
    model2.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"minimumValue"];
    };
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Maximum" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.maximumValue)]] noneInsets];
    model3.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"maximumValue"];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Step" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.stepValue)]];
    model4.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"stepValue"];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Behavior" detailTitle:@"Autorepeat" flag:self.autorepeat] noneInsets];
    model5.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.autorepeat = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Continuous" flag:self.isContinuous] noneInsets];
    model6.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.continuous = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Wrap" flag:self.wraps];
    model7.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.wraps = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model7];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Stepper" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UIScrollView (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self)weakSelf = self;
    
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Style" detailTitle:[DoraemonEnumDescription scrollViewIndicatorStyleDescription:self.indicatorStyle]];
    model1.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription scrollViewIndicatorStyles] currentAction:[DoraemonEnumDescription scrollViewIndicatorStyleDescription:weakSelf.indicatorStyle] completion:^(NSInteger index) {
            weakSelf.indicatorStyle = index;
        }];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Indicators" detailTitle:@"Shows Horizontal Indicator" flag:self.showsHorizontalScrollIndicator] noneInsets];
    model2.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.showsHorizontalScrollIndicator = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Shows Vertical Indicator" flag:self.showsVerticalScrollIndicator];
    model3.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.showsVerticalScrollIndicator = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Scrolling" detailTitle:@"Enable" flag:self.isScrollEnabled] noneInsets];
    model4.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.scrollEnabled = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Paging" flag:self.isPagingEnabled] noneInsets];
    model5.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.pagingEnabled = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Direction Lock" flag:self.isDirectionalLockEnabled];
    model6.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.directionalLockEnabled = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Bounce" detailTitle:@"Bounces" flag:self.bounces] noneInsets];
    model7.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.bounces = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model7];
    
    DoraemonHierarchyCellModel *model8 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Bounce Horizontal" flag:self.alwaysBounceHorizontal] noneInsets];
    model8.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.alwaysBounceHorizontal = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model8];
    
    DoraemonHierarchyCellModel *model9 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Bounce Vertical" flag:self.alwaysBounceVertical];
    model9.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.alwaysBounceVertical = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model9];
    
    DoraemonHierarchyCellModel *model10 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Zoom Min" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.minimumZoomScale)]] noneInsets];
    model10.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"minimumZoomScale"];
    };
    [settings addObject:model10];
    
    DoraemonHierarchyCellModel *model11 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Max" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.maximumZoomScale)]];
    model11.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"maximumZoomScale"];
    };
    [settings addObject:model11];
    
    DoraemonHierarchyCellModel *model12 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Touch" detailTitle:[NSString stringWithFormat:@"Zoom Bounces %@",[self doraemon_hierarchyBoolDescription:self.isZoomBouncing]]] noneInsets];
    [settings addObject:model12];
    
    DoraemonHierarchyCellModel *model13 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Delays Content Touches" flag:self.delaysContentTouches] noneInsets];
    model13.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.delaysContentTouches = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model13];
    
    DoraemonHierarchyCellModel *model14 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Cancellable Content Touches" flag:self.canCancelContentTouches];
    model14.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.canCancelContentTouches = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model14];
    
    DoraemonHierarchyCellModel *model15 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Keyboard" detailTitle:[DoraemonEnumDescription scrollViewKeyboardDismissModeDescription:self.keyboardDismissMode]];
    model15.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription scrollViewKeyboardDismissModes] currentAction:[DoraemonEnumDescription scrollViewKeyboardDismissModeDescription:weakSelf.keyboardDismissMode] completion:^(NSInteger index) {
            weakSelf.keyboardDismissMode = index;
        }];
    };
    [settings addObject:model15];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Scroll View" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UITableView (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self)weakSelf = self;
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Sections" detailTitle:[NSString stringWithFormat:@"%ld",(long)self.numberOfSections]] noneInsets];
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Style" detailTitle:[DoraemonEnumDescription tableViewStyleDescription:self.style]] noneInsets];
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Separator" detailTitle:[DoraemonEnumDescription tableViewCellSeparatorStyleDescription:self.separatorStyle]] noneInsets];
    model3.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription tableViewCellSeparatorStyles] currentAction:[DoraemonEnumDescription tableViewCellSeparatorStyleDescription:weakSelf.separatorStyle] completion:^(NSInteger index) {
            weakSelf.separatorStyle = index;
        }];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[self doraemon_hierarchyColorDescription:self.separatorColor]];
    model4.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"separatorColor"];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Data Source" detailTitle:[self doraemon_hierarchyObjectDescription:self.dataSource]] noneInsets];
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Delegate" detailTitle:[self doraemon_hierarchyObjectDescription:self.delegate]];
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Separator Inset" detailTitle:[self doraemon_hierarchyInsetsTopBottomDescription:self.separatorInset]] noneInsets];
    model7.block = ^{
        [weakSelf doraemon_showEdgeInsetsAndAutomicSetWithKeyPath:@"separatorInset"];
    };
    [settings addObject:model7];
    
    DoraemonHierarchyCellModel *model8 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[self doraemon_hierarchyInsetsLeftRightDescription:self.separatorInset]] noneInsets];
    [settings addObject:model8];
    
    if (@available(iOS 11.0, *)) {
        DoraemonHierarchyCellModel *model9 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[DoraemonEnumDescription tableViewSeparatorInsetReferenceDescription:self.separatorInsetReference]];
        model9.block = ^{
            [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription tableViewSeparatorInsetReferences] currentAction:[DoraemonEnumDescription tableViewSeparatorInsetReferenceDescription:weakSelf.separatorInsetReference] completion:^(NSInteger index) {
                weakSelf.separatorInsetReference = index;
            }];
        };
        [settings addObject:model9];
    }
    
    DoraemonHierarchyCellModel *model10 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Selection" detailTitle:self.allowsSelection ? @"Allowed" : @"Disabled" flag:self.allowsSelection] noneInsets];
    model10.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.allowsSelection = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model10];
    
    DoraemonHierarchyCellModel *model11 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[NSString stringWithFormat:@"Multiple Selection %@",self.allowsMultipleSelection ? @"" : @"Disabled"] flag:self.allowsMultipleSelection] noneInsets];
    model11.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.allowsMultipleSelection = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model11];
    
    DoraemonHierarchyCellModel *model12 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Edit Selection" detailTitle:self.allowsSelectionDuringEditing ? @"Allowed" : @"Disabled" flag:self.allowsSelectionDuringEditing] noneInsets];
    model12.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.allowsSelectionDuringEditing = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model12];
    
    DoraemonHierarchyCellModel *model13 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[NSString stringWithFormat:@"Multiple Selection %@",self.allowsMultipleSelectionDuringEditing ? @"" : @"Disabled"] flag:self.allowsMultipleSelectionDuringEditing];
    model13.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.allowsMultipleSelectionDuringEditing = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model13];
    
    DoraemonHierarchyCellModel *model14 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Min Display" detailTitle:[NSString stringWithFormat:@"%ld",(long)self.sectionIndexMinimumDisplayRowCount]] noneInsets];
    model14.block = ^{
        [weakSelf doraemon_showIntAlertAndAutomicSetWithKeyPath:@"sectionIndexMinimumDisplayRowCount"];
    };
    [settings addObject:model14];
    
    DoraemonHierarchyCellModel *model15 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Text" detailTitle:[self doraemon_hierarchyColorDescription:self.sectionIndexColor]] noneInsets];
    model15.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"sectionIndexColor"];
    };
    [settings addObject:model15];
    
    DoraemonHierarchyCellModel *model16 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Background" detailTitle:[self doraemon_hierarchyColorDescription:self.sectionIndexBackgroundColor]] noneInsets];
    model16.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"sectionIndexBackgroundColor"];
    };
    [settings addObject:model16];
    
    DoraemonHierarchyCellModel *model17 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Tracking" detailTitle:[self doraemon_hierarchyColorDescription:self.sectionIndexTrackingBackgroundColor]];
    model17.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"sectionIndexTrackingBackgroundColor"];
    };
    model17.block = ^{
        
    };
    [settings addObject:model17];
    
    DoraemonHierarchyCellModel *model18 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Row Height" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.rowHeight)]] noneInsets];
    model18.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"rowHeight"];
    };
    [settings addObject:model18];
    
    DoraemonHierarchyCellModel *model19 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Section Header" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.sectionHeaderHeight)]] noneInsets];
    model19.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"sectionHeaderHeight"];
    };
    [settings addObject:model19];
    
    DoraemonHierarchyCellModel *model20 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Section Footer" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.sectionFooterHeight)]];
    model20.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"sectionFooterHeight"];
    };
    [settings addObject:model20];

    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Table View" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UITableViewCell (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self) weakSelf = self;
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Image" detailTitle:[self doraemon_hierarchyImageDescription:self.imageView.image]];
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Identifier" detailTitle:[self doraemon_hierarchyTextDescription:self.reuseIdentifier]];
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Selection" detailTitle:[DoraemonEnumDescription tableViewCellSelectionStyleDescription:self.selectionStyle]] noneInsets];
    model3.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription tableViewCellSelectionStyles] currentAction:[DoraemonEnumDescription tableViewCellSelectionStyleDescription:weakSelf.selectionStyle] completion:^(NSInteger index) {
            weakSelf.selectionStyle = index;
        }];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Accessory" detailTitle:[DoraemonEnumDescription tableViewCellAccessoryTypeDescription:self.accessoryType]] noneInsets];
    model4.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription tableViewCellAccessoryTypes] currentAction:[DoraemonEnumDescription tableViewCellAccessoryTypeDescription:weakSelf.accessoryType] completion:^(NSInteger index) {
            weakSelf.accessoryType = index;
        }];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Editing Acc." detailTitle:[DoraemonEnumDescription tableViewCellAccessoryTypeDescription:self.editingAccessoryType]];
    model5.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription tableViewCellAccessoryTypes] currentAction:[DoraemonEnumDescription tableViewCellAccessoryTypeDescription:weakSelf.editingAccessoryType] completion:^(NSInteger index) {
            weakSelf.editingAccessoryType = index;
        }];
    };
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Indentation" detailTitle:[NSString stringWithFormat:@"%ld",(long)self.indentationLevel]] noneInsets];
    model6.block = ^{
        [weakSelf doraemon_showIntAlertAndAutomicSetWithKeyPath:@"indentationLevel"];
    };
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.indentationWidth)]] noneInsets];
    model7.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"indentationWidth"];
    };
    [settings addObject:model7];
    
    DoraemonHierarchyCellModel *model8 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Indent While Editing" flag:self.shouldIndentWhileEditing] noneInsets];
    model8.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.shouldIndentWhileEditing = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model8];
    
    DoraemonHierarchyCellModel *model9 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Shows Re-order Controls" flag:self.showsReorderControl];
    model9.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.showsReorderControl = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model9];
    
    DoraemonHierarchyCellModel *model10 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Separator Inset" detailTitle:[self doraemon_hierarchyInsetsTopBottomDescription:self.separatorInset]] noneInsets];
    model10.block = ^{
        [weakSelf doraemon_showEdgeInsetsAndAutomicSetWithKeyPath:@"separatorInset"];
    };
    [settings addObject:model10];
    
    DoraemonHierarchyCellModel *model11 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[self doraemon_hierarchyInsetsLeftRightDescription:self.separatorInset]];
    [settings addObject:model11];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Table View Cell" items:settings];
    
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UICollectionView (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Sections" detailTitle:[NSString stringWithFormat:@"%ld",(long)self.numberOfSections]];
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Delegate" detailTitle:[self doraemon_hierarchyObjectDescription:self.delegate]] noneInsets];
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Data Source" detailTitle:[self doraemon_hierarchyObjectDescription:self.dataSource]] noneInsets];
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Layout" detailTitle:[self doraemon_hierarchyObjectDescription:self.collectionViewLayout]];
    [settings addObject:model4];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Collection View" items:settings];
                                
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UICollectionReusableView (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Identifier" detailTitle:[self doraemon_hierarchyTextDescription:self.reuseIdentifier]] noneInsets];
    [settings addObject:model1];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Collection Reusable View" items:settings];
                                
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UITextView (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self)weakSelf = self;
    
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Plain Text" detailTitle:[self doraemon_hierarchyTextDescription:self.text]] noneInsets];
    model1.block = ^{
        [weakSelf doraemon_showTextAlertAndAutomicSetWithKeyPath:@"text"];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Attributed Text" detailTitle:[self doraemon_hierarchyObjectDescription:self.attributedText]] noneInsets];
    model2.block = ^{
        [weakSelf doraemon_showAttributeTextAlertAndAutomicSetWithKeyPath:@"attributedText"];
    };
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Allows Editing Attributes" flag:self.allowsEditingTextAttributes] noneInsets];
    model3.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.allowsEditingTextAttributes = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Color" detailTitle:[self doraemon_hierarchyColorDescription:self.textColor]] noneInsets];
    model4.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"textColor"];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Font" detailTitle:[self doraemon_hierarchyObjectDescription:self.font]] noneInsets];
    model5.block = ^{
        [weakSelf doraemon_showFontAlertAndAutomicSetWithKeyPath:@"font"];
    };
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Alignment" detailTitle:[DoraemonEnumDescription textAlignmentDescription:self.textAlignment]];
    model6.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription textAlignments] currentAction:[DoraemonEnumDescription textAlignmentDescription:weakSelf.textAlignment] completion:^(NSInteger index) {
            weakSelf.textAlignment = index;
        }];
    };
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Behavior" detailTitle:@"Editable" flag:self.isEditable] noneInsets];
    model7.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.editable = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model7];
    
    DoraemonHierarchyCellModel *model8 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Selectable" flag:self.isSelectable];
    model8.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.selectable = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model8];
    
    DoraemonHierarchyCellModel *model9 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Data Detectors" detailTitle:@"Phone Number" flag:self.dataDetectorTypes & UIDataDetectorTypePhoneNumber] noneInsets];
    model9.changePropertyBlock = ^(id  _Nullable obj) {
        if ([obj boolValue]) {
            weakSelf.dataDetectorTypes = weakSelf.dataDetectorTypes | UIDataDetectorTypePhoneNumber;
        } else {
            weakSelf.dataDetectorTypes = weakSelf.dataDetectorTypes & ~UIDataDetectorTypePhoneNumber;
        }
    };
    [settings addObject:model9];
    
    DoraemonHierarchyCellModel *model10 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Link" flag:self.dataDetectorTypes & UIDataDetectorTypeLink] noneInsets];
    model10.changePropertyBlock = ^(id  _Nullable obj) {
        if ([obj boolValue]) {
            weakSelf.dataDetectorTypes = weakSelf.dataDetectorTypes | UIDataDetectorTypeLink;
        } else {
            weakSelf.dataDetectorTypes = weakSelf.dataDetectorTypes & ~UIDataDetectorTypeLink;
        }
    };
    [settings addObject:model10];
    
    DoraemonHierarchyCellModel *model11 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Address" flag:self.dataDetectorTypes & UIDataDetectorTypeAddress] noneInsets];
    model11.changePropertyBlock = ^(id  _Nullable obj) {
        if ([obj boolValue]) {
            weakSelf.dataDetectorTypes = weakSelf.dataDetectorTypes | UIDataDetectorTypeAddress;
        } else {
            weakSelf.dataDetectorTypes = weakSelf.dataDetectorTypes & ~UIDataDetectorTypeAddress;
        }
    };
    [settings addObject:model11];
    
    DoraemonHierarchyCellModel *model12 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Calendar Event" flag:self.dataDetectorTypes & UIDataDetectorTypeCalendarEvent] noneInsets];
    model12.changePropertyBlock = ^(id  _Nullable obj) {
        if ([obj boolValue]) {
            weakSelf.dataDetectorTypes = weakSelf.dataDetectorTypes | UIDataDetectorTypeCalendarEvent;
        } else {
            weakSelf.dataDetectorTypes = weakSelf.dataDetectorTypes & ~UIDataDetectorTypeCalendarEvent;
        }
    };
    [settings addObject:model12];
    
    if (@available(iOS 10.0, *)) {
        DoraemonHierarchyCellModel *model13 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Shipment Tracking Number" flag:self.dataDetectorTypes & UIDataDetectorTypeShipmentTrackingNumber] noneInsets];
        model13.changePropertyBlock = ^(id  _Nullable obj) {
            if ([obj boolValue]) {
                weakSelf.dataDetectorTypes = weakSelf.dataDetectorTypes | UIDataDetectorTypeShipmentTrackingNumber;
            } else {
                weakSelf.dataDetectorTypes = weakSelf.dataDetectorTypes & ~UIDataDetectorTypeShipmentTrackingNumber;
            }
        };
        [settings addObject:model13];
        
        DoraemonHierarchyCellModel *model14 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Flight Number" flag:self.dataDetectorTypes & UIDataDetectorTypeFlightNumber] noneInsets];
        model14.changePropertyBlock = ^(id  _Nullable obj) {
            if ([obj boolValue]) {
                weakSelf.dataDetectorTypes = weakSelf.dataDetectorTypes | UIDataDetectorTypeFlightNumber;
            } else {
                weakSelf.dataDetectorTypes = weakSelf.dataDetectorTypes & ~UIDataDetectorTypeFlightNumber;
            }
        };
        [settings addObject:model14];
        
        DoraemonHierarchyCellModel *model15 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Lookup Suggestion" flag:self.dataDetectorTypes & UIDataDetectorTypeLookupSuggestion];
        model15.changePropertyBlock = ^(id  _Nullable obj) {
            if ([obj boolValue]) {
                weakSelf.dataDetectorTypes = weakSelf.dataDetectorTypes | UIDataDetectorTypeLookupSuggestion;
            } else {
                weakSelf.dataDetectorTypes = weakSelf.dataDetectorTypes & ~UIDataDetectorTypeLookupSuggestion;
            }
        };
        [settings addObject:model15];
    } else {
        [model12 normalInsets];
    }
    
    DoraemonHierarchyCellModel *model16 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Capitalization" detailTitle:[DoraemonEnumDescription textAutocapitalizationTypeDescription:self.autocapitalizationType]] noneInsets];
    model16.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription textAutocapitalizationTypes] currentAction:[DoraemonEnumDescription textAutocapitalizationTypeDescription:weakSelf.autocapitalizationType] completion:^(NSInteger index) {
            weakSelf.autocapitalizationType = index;
        }];
    };
    [settings addObject:model16];
    
    DoraemonHierarchyCellModel *model17 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Correction" detailTitle:[DoraemonEnumDescription textAutocorrectionTypeDescription:self.autocorrectionType]] noneInsets];
    model17.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription textAutocorrectionTypes] currentAction:[DoraemonEnumDescription textAutocorrectionTypeDescription:weakSelf.autocorrectionType] completion:^(NSInteger index) {
            weakSelf.autocorrectionType = index;
        }];
    };
    [settings addObject:model17];
    
    DoraemonHierarchyCellModel *model18 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Keyboard" detailTitle:[DoraemonEnumDescription keyboardTypeDescription:self.keyboardType]] noneInsets];
    model18.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription keyboardTypes] currentAction:[DoraemonEnumDescription keyboardTypeDescription:weakSelf.keyboardType] completion:^(NSInteger index) {
            weakSelf.keyboardType = index;
        }];
    };
    [settings addObject:model18];
    
    DoraemonHierarchyCellModel *model19 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Appearance" detailTitle:[DoraemonEnumDescription keyboardAppearanceDescription:self.keyboardAppearance]] noneInsets];
    model19.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription keyboardAppearances] currentAction:[DoraemonEnumDescription keyboardAppearanceDescription:weakSelf.keyboardAppearance] completion:^(NSInteger index) {
            weakSelf.keyboardAppearance = index;
        }];
    };
    [settings addObject:model19];
    
    DoraemonHierarchyCellModel *model20 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Return Key" detailTitle:[DoraemonEnumDescription returnKeyTypeDescription:self.returnKeyType]] noneInsets];
    model20.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription returnKeyTypes] currentAction:[DoraemonEnumDescription returnKeyTypeDescription:weakSelf.returnKeyType] completion:^(NSInteger index) {
            weakSelf.returnKeyType = index;
        }];
    };
    [settings addObject:model20];
    
    DoraemonHierarchyCellModel *model21 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Auto-enable Return Key" flag:self.enablesReturnKeyAutomatically] noneInsets];
    model21.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.enablesReturnKeyAutomatically = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model21];
    
    DoraemonHierarchyCellModel *model22 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Secure Entry" flag:self.isSecureTextEntry];
    model22.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.secureTextEntry = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model22];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Text View" items:settings];
                                
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UIDatePicker (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self) weakSelf = self;
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Mode" detailTitle:[DoraemonEnumDescription datePickerModeDescription:self.datePickerMode]] noneInsets];
    model1.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription datePickerModes] currentAction:[DoraemonEnumDescription datePickerModeDescription:weakSelf.datePickerMode] completion:^(NSInteger index) {
            weakSelf.datePickerMode = index;
        }];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Locale Identifier" detailTitle:self.locale.localeIdentifier] noneInsets];
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Interval" detailTitle:[NSString stringWithFormat:@"%ld",(long)self.minuteInterval]];
    model3.block = ^{
        [weakSelf doraemon_showIntAlertAndAutomicSetWithKeyPath:@"minuteInterval"];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Date" detailTitle:[self doraemon_hierarchyDateDescription:self.date]] noneInsets];
    model4.block = ^{
        [weakSelf doraemon_showDateAlertAndAutomicSetWithKeyPath:@"date"];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Min Date" detailTitle:[self doraemon_hierarchyDateDescription:self.minimumDate]] noneInsets];
    model5.block = ^{
        [weakSelf doraemon_showDateAlertAndAutomicSetWithKeyPath:@"minimumDate"];
    };
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Max Date" detailTitle:[self doraemon_hierarchyDateDescription:self.maximumDate]];
    model6.block = ^{
        [weakSelf doraemon_showDateAlertAndAutomicSetWithKeyPath:@"maximumDate"];
    };
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Count Down" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.countDownDuration)]];
    [settings addObject:model7];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Date Picker" items:settings];
                                
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UIPickerView (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Delegate" detailTitle:[self doraemon_hierarchyObjectDescription:self.delegate]];
    [settings addObject:model1];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Picker View" items:settings];
                                
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UINavigationBar (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self)weakSelf = self;
    
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Style" detailTitle:[DoraemonEnumDescription barStyleDescription:self.barStyle]] noneInsets];
    model1.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription barStyles] currentAction:[DoraemonEnumDescription barStyleDescription:weakSelf.barStyle] completion:^(NSInteger index) {
            weakSelf.barStyle = index;
        }];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Translucent" flag:self.isTranslucent] noneInsets];
    model2.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.translucent = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model2];
    
    if (@available(iOS 11.0, *)) {
        DoraemonHierarchyCellModel *model3 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Prefers Large Titles" flag:self.prefersLargeTitles] noneInsets];
        model3.changePropertyBlock = ^(id  _Nullable obj) {
            weakSelf.prefersLargeTitles = [obj boolValue];
            [weakSelf doraemon_postHierarchyChangeNotification];
        };
        [settings addObject:model3];
    }
    
    DoraemonHierarchyCellModel *model4 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Bar Tint" detailTitle:[self doraemon_hierarchyColorDescription:self.barTintColor]] noneInsets];
    model4.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"barTintColor"];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Shadow Image" detailTitle:[self doraemon_hierarchyImageDescription:self.shadowImage]] noneInsets];
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Back Image" detailTitle:[self doraemon_hierarchyImageDescription:self.backIndicatorImage]] noneInsets];
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Back Mask" detailTitle:[self doraemon_hierarchyImageDescription:self.backIndicatorTransitionMaskImage]];
    [settings addObject:model7];
    
    DoraemonHierarchyCellModel *model8 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Title Attr." detailTitle:nil] noneInsets];
    [settings addObject:model8];
    
    DoraemonHierarchyCellModel *model9 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Title Font" detailTitle:[self doraemon_hierarchyObjectDescription:self.titleTextAttributes[NSFontAttributeName]]] noneInsets];
    if (self.titleTextAttributes[NSFontAttributeName]) {
        model9.block = ^{
            __block UIFont *font = weakSelf.titleTextAttributes[NSFontAttributeName];
            if (!font) {
                return;
            }
            [weakSelf doraemon_showTextFieldAlertWithText:[NSString stringWithFormat:@"%@",[DoraemonHierarchyFormatterTool formatNumber:@(font.pointSize)]] handler:^(NSString * _Nullable newText) {
                NSMutableDictionary *attributes = [[NSMutableDictionary alloc] initWithDictionary:weakSelf.titleTextAttributes];
                attributes[NSFontAttributeName] = [font fontWithSize:[newText doubleValue]];
                weakSelf.titleTextAttributes = [attributes copy];
            }];
        };
    }
    [settings addObject:model9];
    
    DoraemonHierarchyCellModel *model10 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Title Color" detailTitle:[self doraemon_hierarchyColorDescription:self.titleTextAttributes[NSForegroundColorAttributeName]]] noneInsets];
    [settings addObject:model10];
    
    NSShadow *shadow = self.titleTextAttributes[NSShadowAttributeName];
    if (![shadow isKindOfClass:[NSShadow class]]) {
        shadow = nil;
    }
    
    DoraemonHierarchyCellModel *model11 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Shadow" detailTitle:[self doraemon_hierarchyColorDescription:shadow.shadowColor]] noneInsets];
    [settings addObject:model11];
    
    DoraemonHierarchyCellModel *model12 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Shadow Offset" detailTitle:[self doraemon_hierarchySizeDescription:shadow.shadowOffset]];
    [settings addObject:model12];
    
    if (@available(iOS 11.0, *)) {
        DoraemonHierarchyCellModel *model13 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Large Title Attr." detailTitle:nil] noneInsets];
        [settings addObject:model13];
        
        DoraemonHierarchyCellModel *model14 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Title Font" detailTitle:[self doraemon_hierarchyColorDescription:self.largeTitleTextAttributes[NSFontAttributeName]]] noneInsets];
        if (self.largeTitleTextAttributes[NSFontAttributeName]) {
            model14.block = ^{
                __block UIFont *font = weakSelf.largeTitleTextAttributes[NSFontAttributeName];
                if (!font) {
                    return;
                }
                [weakSelf doraemon_showTextFieldAlertWithText:[NSString stringWithFormat:@"%@",[DoraemonHierarchyFormatterTool formatNumber:@(font.pointSize)]] handler:^(NSString * _Nullable newText) {
                    NSMutableDictionary *attributes = [[NSMutableDictionary alloc] initWithDictionary:weakSelf.largeTitleTextAttributes];
                    attributes[NSFontAttributeName] = [font fontWithSize:[newText doubleValue]];
                    weakSelf.largeTitleTextAttributes = [attributes copy];
                }];
            };
        }
        [settings addObject:model14];
        
        DoraemonHierarchyCellModel *model15 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Title Color" detailTitle:[self doraemon_hierarchyColorDescription:self.largeTitleTextAttributes[NSForegroundColorAttributeName]]] noneInsets];
        [settings addObject:model15];
        
        shadow = self.largeTitleTextAttributes[NSShadowAttributeName];
        if (![shadow isKindOfClass:[NSShadow class]]) {
            shadow = nil;
        }
        
        DoraemonHierarchyCellModel *model16 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Shadow" detailTitle:[self doraemon_hierarchyColorDescription:shadow.shadowColor]] noneInsets];
        [settings addObject:model16];
        
        DoraemonHierarchyCellModel *model17 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Shadow Offset" detailTitle:[self doraemon_hierarchySizeDescription:shadow.shadowOffset]];
        [settings addObject:model17];
    }
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Navigation Bar" items:settings];
                                
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UIToolbar (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self) weakSelf = self;
    
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Style" detailTitle:[DoraemonEnumDescription barStyleDescription:self.barStyle]] noneInsets];
    model1.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription barStyles] currentAction:[DoraemonEnumDescription barStyleDescription:weakSelf.barStyle] completion:^(NSInteger index) {
            weakSelf.barStyle = index;
        }];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Translucent" flag:self.isTranslucent] noneInsets];
    model2.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.translucent = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Bar Tint" detailTitle:[self doraemon_hierarchyColorDescription:self.barTintColor]];
    model3.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"barTintColor"];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Tool Bar" items:settings];
                                
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UITabBar (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self) weakSelf = self;
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Background" detailTitle:[self doraemon_hierarchyImageDescription:self.backgroundImage]] noneInsets];
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Shadow" detailTitle:[self doraemon_hierarchyImageDescription:self.shadowImage]] noneInsets];
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Selection" detailTitle:[self doraemon_hierarchyImageDescription:self.selectionIndicatorImage]];
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Style" detailTitle:[DoraemonEnumDescription barStyleDescription:self.barStyle]] noneInsets];
    model4.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription barStyles] currentAction:[DoraemonEnumDescription barStyleDescription:weakSelf.barStyle] completion:^(NSInteger index) {
            weakSelf.barStyle = index;
        }];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Translucent" flag:self.isTranslucent] noneInsets];
    model5.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.translucent = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Bar Tint" detailTitle:[self doraemon_hierarchyColorDescription:self.barTintColor]];
    model6.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"barTintColor"];
    };
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Style" detailTitle:[DoraemonEnumDescription tabBarItemPositioningDescription:self.itemPositioning]] noneInsets];
    model7.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription tabBarItemPositionings] currentAction:[DoraemonEnumDescription tabBarItemPositioningDescription:weakSelf.itemPositioning] completion:^(NSInteger index) {
            weakSelf.itemPositioning = index;
        }];
    };
    [settings addObject:model7];
    
    DoraemonHierarchyCellModel *model8 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Item Width" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.itemWidth)]] noneInsets];
    model8.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"itemWidth"];
    };
    [settings addObject:model8];
    
    DoraemonHierarchyCellModel *model9 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Item Spacing" detailTitle:[DoraemonHierarchyFormatterTool formatNumber:@(self.itemSpacing)]];
    model9.block = ^{
        [weakSelf doraemon_showDoubleAlertAndAutomicSetWithKeyPath:@"itemSpacing"];
    };
    [settings addObject:model9];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Tab Bar" items:settings];
                                
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UISearchBar (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    __weak typeof(self) weakSelf = self;
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Text" detailTitle:[self doraemon_hierarchyTextDescription:self.text]] noneInsets];
    model1.block = ^{
        [weakSelf doraemon_showTextAlertAndAutomicSetWithKeyPath:@"text"];
    };
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Placeholder" detailTitle:[self doraemon_hierarchyTextDescription:self.placeholder]] noneInsets];
    model2.block = ^{
        [weakSelf doraemon_showTextAlertAndAutomicSetWithKeyPath:@"placeholder"];
    };
    [settings addObject:model2];
    
    DoraemonHierarchyCellModel *model3 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Prompt" detailTitle:[self doraemon_hierarchyTextDescription:self.prompt]];
    model3.block = ^{
        [weakSelf doraemon_showTextAlertAndAutomicSetWithKeyPath:@"prompt"];
    };
    [settings addObject:model3];
    
    DoraemonHierarchyCellModel *model4 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Search Style" detailTitle:[DoraemonEnumDescription searchBarStyleDescription:self.searchBarStyle]] noneInsets];
    model4.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription searchBarStyles] currentAction:[DoraemonEnumDescription searchBarStyleDescription:weakSelf.searchBarStyle] completion:^(NSInteger index) {
            weakSelf.searchBarStyle = index;
        }];
    };
    [settings addObject:model4];
    
    DoraemonHierarchyCellModel *model5 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Bar Style" detailTitle:[DoraemonEnumDescription barStyleDescription:self.barStyle]] noneInsets];
    model5.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription barStyles] currentAction:[DoraemonEnumDescription barStyleDescription:weakSelf.barStyle] completion:^(NSInteger index) {
            weakSelf.barStyle = index;
        }];
    };
    [settings addObject:model5];
    
    DoraemonHierarchyCellModel *model6 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Translucent" flag:self.isTranslucent] noneInsets];
    model6.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.translucent = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model6];
    
    DoraemonHierarchyCellModel *model7 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[self doraemon_hierarchyColorDescription:self.barTintColor]];
    model7.block = ^{
        [weakSelf doraemon_showColorAlertAndAutomicSetWithKeyPath:@"barTintColor"];
    };
    [settings addObject:model7];
    
    DoraemonHierarchyCellModel *model8 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Background" detailTitle:[self doraemon_hierarchyImageDescription:self.backgroundImage]] noneInsets];
    [settings addObject:model8];
    
    DoraemonHierarchyCellModel *model9 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Scope Bar" detailTitle:[self doraemon_hierarchyImageDescription:self.scopeBarBackgroundImage]];
    [settings addObject:model9];
    
    DoraemonHierarchyCellModel *model10 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Text Offset" detailTitle:[self doraemon_hierarchyOffsetDescription:self.searchTextPositionAdjustment]] noneInsets];
    [settings addObject:model10];
    
    DoraemonHierarchyCellModel *model11 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"BG Offset" detailTitle:[self doraemon_hierarchyOffsetDescription:self.searchFieldBackgroundPositionAdjustment]];
    [settings addObject:model11];
    
    DoraemonHierarchyCellModel *model12 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Options" detailTitle:@"Shows Search Results Button" flag:self.showsSearchResultsButton] noneInsets];
    model12.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.showsSearchResultsButton = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model12];
    
    DoraemonHierarchyCellModel *model13 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Shows Bookmarks Button" flag:self.showsBookmarkButton] noneInsets];
    model13.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.showsBookmarkButton = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model13];
    
    DoraemonHierarchyCellModel *model14 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Shows Cancel Button" flag:self.showsCancelButton] noneInsets];
    model14.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.showsCancelButton = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model14];
    
    DoraemonHierarchyCellModel *model15 = [[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:@"Shows Scope Bar" flag:self.showsScopeBar];
    model15.changePropertyBlock = ^(id  _Nullable obj) {
        weakSelf.showsScopeBar = [obj boolValue];
        [weakSelf doraemon_postHierarchyChangeNotification];
    };
    [settings addObject:model15];
    
    DoraemonHierarchyCellModel *model16 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Scope Titles" detailTitle:[self doraemon_hierarchyObjectDescription:self.scopeButtonTitles]];
    [settings addObject:model16];
    
    DoraemonHierarchyCellModel *model17 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Capitalization" detailTitle:[DoraemonEnumDescription textAutocapitalizationTypeDescription:self.autocapitalizationType]] noneInsets];
    model17.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription textAutocapitalizationTypes] currentAction:[DoraemonEnumDescription textAutocapitalizationTypeDescription:weakSelf.autocapitalizationType] completion:^(NSInteger index) {
            weakSelf.autocapitalizationType = index;
        }];
    };
    [settings addObject:model17];
    
    DoraemonHierarchyCellModel *model18 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:@"Correction" detailTitle:[DoraemonEnumDescription textAutocorrectionTypeDescription:self.autocorrectionType]] noneInsets];
    model18.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription textAutocorrectionTypes] currentAction:[DoraemonEnumDescription textAutocorrectionTypeDescription:weakSelf.autocorrectionType] completion:^(NSInteger index) {
            weakSelf.autocorrectionType = index;
        }];
    };
    [settings addObject:model18];
    
    DoraemonHierarchyCellModel *model19 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Keyboard" detailTitle:[DoraemonEnumDescription keyboardTypeDescription:self.keyboardType]];
    model19.block = ^{
        [weakSelf doraemon_showActionSheetWithActions:[DoraemonEnumDescription keyboardTypes] currentAction:[DoraemonEnumDescription keyboardTypeDescription:weakSelf.keyboardType] completion:^(NSInteger index) {
            weakSelf.keyboardType = index;
        }];
    };
    [settings addObject:model19];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Search Bar" items:settings];
                                
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end

@implementation UIWindow (DoraemonHierarchy)

- (NSArray<DoraemonHierarchyCategoryModel *> *)doraemon_hierarchyCategoryModels {
    NSMutableArray *settings = [[NSMutableArray alloc] init];
    
    DoraemonHierarchyCellModel *model1 = [[[DoraemonHierarchyCellModel alloc] initWithTitle:nil detailTitle:[NSString stringWithFormat:@"Key Window %@",[self doraemon_hierarchyBoolDescription:self.isKeyWindow]]] noneInsets];
    [settings addObject:model1];
    
    DoraemonHierarchyCellModel *model2 = [[DoraemonHierarchyCellModel alloc] initWithTitle:@"Root Controller" detailTitle:[self doraemon_hierarchyObjectDescription:self.rootViewController]];
    [settings addObject:model2];
    
    DoraemonHierarchyCategoryModel *model = [[DoraemonHierarchyCategoryModel alloc] initWithTitle:@"Window" items:settings];
                                
    NSMutableArray *models = [[NSMutableArray alloc] initWithArray:[super doraemon_hierarchyCategoryModels]];
    if (models.count > 0) {
        [models insertObject:model atIndex:1];
    } else {
        [models addObject:model];
    }
    return [models copy];
}

@end
