//
//  DoraemonEnumDescription.m
//  DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonEnumDescription.h"

@implementation DoraemonEnumDescription

+ (NSString *)lineBreakModeDescription:(NSLineBreakMode)mode {
    switch (mode) {
        case NSLineBreakByWordWrapping:
            return @"Word Wrapping";
        case NSLineBreakByCharWrapping:
            return @"Char Wrapping";
        case NSLineBreakByClipping:
            return @"Clipping";
        case NSLineBreakByTruncatingHead:
            return @"Truncating Head";
        case NSLineBreakByTruncatingMiddle:
            return @"Truncating Middle";
        case NSLineBreakByTruncatingTail:
            return @"Truncation Tail";
    }
    return nil;
}

+ (NSArray <NSString *>*)lineBreaks {
    NSMutableArray *breaks = [[NSMutableArray alloc] init];
    for (NSLineBreakMode i = NSLineBreakByWordWrapping; i <= NSLineBreakByTruncatingMiddle; i++) {
        NSString *lineBreak = [self lineBreakModeDescription:i];
        if (lineBreak) {
            [breaks addObject:lineBreak];
        }
    }
    return [breaks copy];
}

+ (NSString *)userInterfaceStyleDescription:(UIUserInterfaceStyle)style {
    switch (style) {
        case UIUserInterfaceStyleUnspecified:
            return @"Unspecified";
        case UIUserInterfaceStyleLight:
            return @"Light User Interface Style";
        case UIUserInterfaceStyleDark:
            return @"Dark User Interface Style";
    }
    return nil;
}

+ (NSString *)userInterfaceSizeClassDescription:(UIUserInterfaceSizeClass)sizeClass {
    switch (sizeClass) {
        case UIUserInterfaceSizeClassUnspecified:
            return @"Unspecified";
        case UIUserInterfaceSizeClassCompact:
            return @"Compact Size Class";
        case UIUserInterfaceSizeClassRegular:
            return @"Regular Size Class";
    }
    return nil;
}

+ (NSString *)traitEnvironmentLayoutDirectionDescription:(UITraitEnvironmentLayoutDirection)direction {
    switch (direction) {
        case UITraitEnvironmentLayoutDirectionUnspecified:
            return @"Unspecified";
        case UITraitEnvironmentLayoutDirectionLeftToRight:
            return @"Left To Right Layout";
        case UITraitEnvironmentLayoutDirectionRightToLeft:
            return @"Right To Left Layout";
    }
    return nil;
}

+ (NSString *)viewContentModeDescription:(UIViewContentMode)mode {
    switch (mode) {
        case UIViewContentModeScaleToFill:
            return @"ScaleToFill";
        case UIViewContentModeScaleAspectFit:
            return @"ScaleAspectFit";
        case UIViewContentModeScaleAspectFill:
            return @"ScaleAspectFill";
        case UIViewContentModeRedraw:
            return @"Redraw";
        case UIViewContentModeCenter:
            return @"Center";
        case UIViewContentModeTop:
            return @"Top";
        case UIViewContentModeBottom:
            return @"Bottom";
        case UIViewContentModeLeft:
            return @"Left";
        case UIViewContentModeRight:
            return @"Right";
        case UIViewContentModeTopLeft:
            return @"TopLeft";
        case UIViewContentModeTopRight:
            return @"TopRight";
        case UIViewContentModeBottomLeft:
            return @"BottomLeft";
        case UIViewContentModeBottomRight:
            return @"BottomRight";
    }
    return nil;
}

+ (NSArray <NSString *>*)viewContentModeDescriptions {
    NSMutableArray *descriptions = [[NSMutableArray alloc] init];
    for (UIViewContentMode i = UIViewContentModeScaleToFill; i <= UIViewContentModeBottomRight; i++) {
        NSString *description = [self viewContentModeDescription:i];
        if (description) {
            [descriptions addObject:description];
        }
    }
    return [descriptions copy];
}

+ (NSString *)textAlignmentDescription:(NSTextAlignment)textAlignment {
    switch (textAlignment) {
        case NSTextAlignmentLeft:
            return @"Left";
        case NSTextAlignmentRight:
            return @"Right";
        case NSTextAlignmentCenter:
            return @"Center";
        case NSTextAlignmentJustified:
            return @"Justified";
        case NSTextAlignmentNatural:
            return @"Natural";
    }
    return nil;
}

+ (NSArray <NSString *>*)textAlignments {
    NSMutableArray *alignments = [[NSMutableArray alloc] init];
    for (NSTextAlignment i = NSTextAlignmentLeft; i <= NSTextAlignmentNatural; i++) {
        NSString *alignment = [self textAlignmentDescription:i];
        if (alignment) {
            [alignments addObject:alignment];
        }
    }
    return [alignments copy];
}

+ (NSString *)baselineAdjustmentDescription:(UIBaselineAdjustment)baselineAdjustment {
    switch (baselineAdjustment) {
        case UIBaselineAdjustmentAlignBaselines:
            return @"Baselines";
        case UIBaselineAdjustmentAlignCenters:
            return @"Centers";
        case UIBaselineAdjustmentNone:
            return @"None";
    }
    return nil;
}

+ (NSArray <NSString *>*)baselineAdjustments {
    NSMutableArray *adjustments = [[NSMutableArray alloc] init];
    for (UIBaselineAdjustment i = UIBaselineAdjustmentAlignBaselines; i <= UIBaselineAdjustmentNone; i++) {
        NSString *adjustment = [self baselineAdjustmentDescription:i];
        if (adjustment) {
            [adjustments addObject:adjustment];
        }
    }
    return adjustments;
}

+ (NSString *)controlContentVerticalAlignmentDescription:(UIControlContentVerticalAlignment)contentVerticalAlignment {
    switch (contentVerticalAlignment) {
        case UIControlContentVerticalAlignmentCenter:
            return @"Centered";
        case UIControlContentVerticalAlignmentTop:
            return @"Top";
        case UIControlContentVerticalAlignmentBottom:
            return @"Bottom";
        case UIControlContentVerticalAlignmentFill:
            return @"Fill";
    }
    return nil;
}

+ (NSArray <NSString *>*)controlContentVerticalAlignments {
    NSMutableArray *alignments = [[NSMutableArray alloc] init];
    for (UIControlContentVerticalAlignment i = UIControlContentVerticalAlignmentCenter; i <= UIControlContentVerticalAlignmentFill; i++) {
        NSString *alignment = [self controlContentVerticalAlignmentDescription:i];
        if (alignment) {
            [alignments addObject:alignment];
        }
    }
    return [alignments copy];
}

+ (NSString *)controlContentHorizontalAlignmentDescription:(UIControlContentHorizontalAlignment)contentHorizontalAlignment {
    switch (contentHorizontalAlignment) {
        case UIControlContentHorizontalAlignmentCenter:
            return @"Centered";
        case UIControlContentHorizontalAlignmentLeft:
            return @"Left";
        case UIControlContentHorizontalAlignmentRight:
            return @"Right";
        case UIControlContentHorizontalAlignmentFill:
            return @"Fill";
        case UIControlContentHorizontalAlignmentLeading:
            return @"Leading";
        case UIControlContentHorizontalAlignmentTrailing:
            return @"Trailing";
    }
    return nil;
}

+ (NSArray <NSString *>*)controlContentHorizontalAlignments {
    NSMutableArray *alignments = [[NSMutableArray alloc] init];
    UIControlContentHorizontalAlignment max = UIControlContentHorizontalAlignmentFill;
    if (@available(iOS 11.0, *)) {
        max = UIControlContentHorizontalAlignmentTrailing;
    }
    for (UIControlContentHorizontalAlignment i = UIControlContentHorizontalAlignmentCenter; i <= max; i++) {
        NSString *alignment = [self controlContentHorizontalAlignmentDescription:i];
        if (alignment) {
            [alignments addObject:alignment];
        }
    }
    return [alignments copy];
}

+ (NSString *)buttonTypeDescription:(UIButtonType)buttonType {
    switch (buttonType) {
        case UIButtonTypeCustom:
            return @"Custom";
        case UIButtonTypeSystem:
            return @"System";
        case UIButtonTypeDetailDisclosure:
            return @"Detail Disclosure";
        case UIButtonTypeInfoLight:
            return @"Info Light";
        case UIButtonTypeInfoDark:
            return @"Info Dark";
        case UIButtonTypeContactAdd:
            return @"Contact Add";
#ifdef __IPHONE_13_0
        case UIButtonTypeClose:
            return @"Close";
#endif
        default:
            break;
    }
    return nil;
}

+ (NSString *)controlStateDescription:(UIControlState)state {
    switch (state) {
        case UIControlStateNormal:
            return @"Normal";
        case UIControlStateFocused:
            return @"Focused";
        case UIControlStateDisabled:
            return @"Disabled";
        case UIControlStateReserved:
            return @"Reserved";
        case UIControlStateSelected:
            return @"Selected";
        case UIControlStateApplication:
            return @"Application";
        case UIControlStateHighlighted:
            return @"Highlighted";
    }
    return nil;
}

+ (NSString *)textBorderStyleDescription:(UITextBorderStyle)style {
    switch (style) {
        case UITextBorderStyleNone:
            return @"None";
        case UITextBorderStyleLine:
            return @"Line";
        case UITextBorderStyleBezel:
            return @"Bezel";
        case UITextBorderStyleRoundedRect:
            return @"Rounded Rect";
    }
    return nil;
}

+ (NSArray <NSString *>*)textBorderStyles {
    NSMutableArray *styles = [[NSMutableArray alloc] init];
    for (UITextBorderStyle i = UITextBorderStyleNone; i <= UITextBorderStyleRoundedRect; i++) {
        NSString *style = [self textBorderStyleDescription:i];
        if (style) {
            [styles addObject:style];
        }
    }
    return [styles copy];
}

+ (NSString *)textFieldViewModeDescription:(UITextFieldViewMode)mode {
    switch (mode) {
        case UITextFieldViewModeNever:
            return @"Never appears";
        case UITextFieldViewModeWhileEditing:
            return @"While editing";
        case UITextFieldViewModeUnlessEditing:
            return @"Unless editing";
        case UITextFieldViewModeAlways:
            return @"Always appears";
    }
    return nil;
}

+ (NSArray <NSString *>*)textFieldViewModes {
    NSMutableArray *modes = [[NSMutableArray alloc] init];
    for (UITextFieldViewMode i = UITextFieldViewModeNever; i <= UITextFieldViewModeAlways; i++) {
        NSString *mode = [self textFieldViewModeDescription:i];
        if (mode) {
            [modes addObject:mode];
        }
    }
    return [modes copy];
}

+ (NSString *)textAutocapitalizationTypeDescription:(UITextAutocapitalizationType)type {
    switch (type) {
        case UITextAutocapitalizationTypeNone:
            return @"None";
        case UITextAutocapitalizationTypeWords:
            return @"Words";
        case UITextAutocapitalizationTypeSentences:
            return @"Sentences";
        case UITextAutocapitalizationTypeAllCharacters:
            return @"All Characters";
    }
    return nil;
}

+ (NSArray <NSString *>*)textAutocapitalizationTypes {
    NSMutableArray *types = [[NSMutableArray alloc] init];
    for (UITextAutocapitalizationType i = UITextAutocapitalizationTypeNone; i <= UITextAutocapitalizationTypeAllCharacters; i++) {
        NSString *type = [self textAutocapitalizationTypeDescription:i];
        if (type) {
            [types addObject:type];
        }
    }
    return [types copy];
}

+ (NSString *)textAutocorrectionTypeDescription:(UITextAutocorrectionType)type {
    switch (type) {
        case UITextAutocorrectionTypeDefault:
            return @"Default";
        case UITextAutocorrectionTypeNo:
            return @"NO";
        case UITextAutocorrectionTypeYes:
            return @"YES";
    }
    return nil;
}

+ (NSArray <NSString *>*)textAutocorrectionTypes {
    NSMutableArray *types = [[NSMutableArray alloc] init];
    for (UITextAutocorrectionType i = 0; i <= UITextAutocorrectionTypeYes; i++) {
        NSString *type = [self textAutocorrectionTypeDescription:i];
        if (type) {
            [types addObject:type];
        }
    }
    return [types copy];
}

+ (NSString *)keyboardTypeDescription:(UIKeyboardType)type {
    switch (type) {
        case UIKeyboardTypeDefault:
            return @"Default";
        case UIKeyboardTypeASCIICapable:
            return @"ASCII capable";
        case UIKeyboardTypeNumbersAndPunctuation:
            return @"Numbers and punctuation";
        case UIKeyboardTypeURL:
            return @"URL";
        case UIKeyboardTypeNumberPad:
            return @"Number pad";
        case UIKeyboardTypePhonePad:
            return @"Phone pad";
        case UIKeyboardTypeNamePhonePad:
            return @"Name phone pad";
        case UIKeyboardTypeEmailAddress:
            return @"Email address";
        case UIKeyboardTypeDecimalPad:
            return @"Decimal pad";
        case UIKeyboardTypeTwitter:
            return @"Twitter";
        case UIKeyboardTypeWebSearch:
            return @"Web search";
        case UIKeyboardTypeASCIICapableNumberPad:
            return @"ASCII capable number pad";
    }
    return nil;
}

+ (NSArray <NSString *>*)keyboardTypes {
    NSMutableArray *types = [[NSMutableArray alloc] init];
    UIKeyboardType max = UIKeyboardTypeWebSearch;
    if (@available(iOS 10.0, *)) {
        max = UIKeyboardTypeASCIICapableNumberPad;
    }
    for (UIKeyboardType i = UIKeyboardTypeDefault; i <= max; i++) {
        NSString *type = [self keyboardTypeDescription:i];
        if (type) {
            [types addObject:type];
        }
    }
    return [types copy];
}

+ (NSString *)keyboardAppearanceDescription:(UIKeyboardAppearance)appearance {
    switch (appearance) {
        case UIKeyboardAppearanceDefault:
            return @"Default";
        case UIKeyboardAppearanceDark:
            return @"Dark";
        case UIKeyboardAppearanceLight:
            return @"Light";
    }
    return nil;
}

+ (NSArray <NSString *>*)keyboardAppearances {
    NSMutableArray *appearances = [[NSMutableArray alloc] init];
    for (UIKeyboardAppearance i = UIKeyboardAppearanceDefault; i <= UIKeyboardAppearanceLight; i++) {
        NSString *appearance = [self keyboardAppearanceDescription:i];
        if (appearance) {
            [appearances addObject:appearance];
        }
    }
    return [appearances copy];
}

+ (NSString *)returnKeyTypeDescription:(UIReturnKeyType)type {
    switch (type) {
        case UIReturnKeyDefault:
            return @"Default";
        case UIReturnKeyGo:
            return @"Go";
        case UIReturnKeyGoogle:
            return @"Google";
        case UIReturnKeyJoin:
            return @"Join";
        case UIReturnKeyNext:
            return @"Next";
        case UIReturnKeyRoute:
            return @"Route";
        case UIReturnKeySearch:
            return @"Search";
        case UIReturnKeySend:
            return @"Send";
        case UIReturnKeyYahoo:
            return @"Yahoo";
        case UIReturnKeyDone:
            return @"Done";
        case UIReturnKeyEmergencyCall:
            return @"Emergency call";
        case UIReturnKeyContinue:
            return @"Continue";
    }
    return nil;
}

+ (NSArray <NSString *>*)returnKeyTypes {
    NSMutableArray *types = [[NSMutableArray alloc] init];
    UIReturnKeyType max = UIReturnKeyEmergencyCall;
    if (@available(iOS 9.0, *)) {
        max = UIReturnKeyContinue;
    }
    for (UIReturnKeyType i = UIReturnKeyDefault; i <= max; i++) {
        NSString *type = [self returnKeyTypeDescription:i];
        if (type) {
            [types addObject:type];
        }
    }
    return [types copy];
}

+ (NSString *)activityIndicatorViewStyleDescription:(UIActivityIndicatorViewStyle)style {
    switch (style) {
#ifdef __IPHONE_13_0
        case UIActivityIndicatorViewStyleMedium:
            return @"Medium";
        case UIActivityIndicatorViewStyleLarge:
            return @"Large";
#endif
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
        case UIActivityIndicatorViewStyleWhiteLarge:
            return @"White Large";
        case UIActivityIndicatorViewStyleWhite:
            return @"White";
        case UIActivityIndicatorViewStyleGray:
            return @"Gray";
#pragma clang diagnostic pop
    }
    return nil;
}

+ (NSArray <NSString *>*)activityIndicatorViewStyles {
    NSMutableArray *styles = [[NSMutableArray alloc] init];
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    NSArray *actions = @[@(UIActivityIndicatorViewStyleWhiteLarge),@(UIActivityIndicatorViewStyleWhite),@(UIActivityIndicatorViewStyleGray)];
#ifdef __IPHONE_13_0
    if (@available(iOS 13.0, *)) {
        actions = @[@(UIActivityIndicatorViewStyleMedium),@(UIActivityIndicatorViewStyleLarge),@(UIActivityIndicatorViewStyleWhiteLarge),@(UIActivityIndicatorViewStyleWhite),@(UIActivityIndicatorViewStyleGray)];
    }
#endif
#pragma clang diagnostic pop
    for (NSNumber *num in actions) {
        NSString *style = [self activityIndicatorViewStyleDescription:[num integerValue]];
        if (style) {
            [styles addObject:style];
        }
    }
    return [styles copy];
}

+ (NSString *)progressViewStyleDescription:(UIProgressViewStyle)style {
    switch (style) {
        case UIProgressViewStyleDefault:
            return @"Default";
        case UIProgressViewStyleBar:
            return @"Bar";
    }
    return nil;
}

+ (NSArray <NSString *>*)progressViewStyles {
    NSMutableArray *styles = [[NSMutableArray alloc] init];
    for (UIProgressViewStyle i = UIProgressViewStyleDefault; i <= UIProgressViewStyleBar; i++) {
        NSString *style = [self progressViewStyleDescription:i];
        if (style) {
            [styles addObject:style];
        }
    }
    return [styles copy];
}

+ (NSString *)scrollViewIndicatorStyleDescription:(UIScrollViewIndicatorStyle)style {
    switch (style) {
        case UIScrollViewIndicatorStyleDefault:
            return @"Default";
        case UIScrollViewIndicatorStyleBlack:
            return @"Black";
        case UIScrollViewIndicatorStyleWhite:
            return @"White";
    }
    return nil;
}

+ (NSArray <NSString *>*)scrollViewIndicatorStyles {
    NSMutableArray *styles = [[NSMutableArray alloc] init];
    for (UIScrollViewIndicatorStyle i = UIScrollViewIndicatorStyleDefault; i <= UIScrollViewIndicatorStyleWhite; i++) {
        NSString *style = [self scrollViewIndicatorStyleDescription:i];
        if (style) {
            [styles addObject:style];
        }
    }
    return [styles copy];
}

+ (NSString *)scrollViewKeyboardDismissModeDescription:(UIScrollViewKeyboardDismissMode)mode {
    switch (mode) {
        case UIScrollViewKeyboardDismissModeNone:
            return @"Do not dismiss";
        case UIScrollViewKeyboardDismissModeOnDrag:
            return @"Dismiss on drag";
        case UIScrollViewKeyboardDismissModeInteractive:
            return @"Dismiss interactively";
    }
    return nil;
}

+ (NSArray <NSString *>*)scrollViewKeyboardDismissModes {
    NSMutableArray *modes = [[NSMutableArray alloc] init];
    for (UIScrollViewKeyboardDismissMode i = UIScrollViewKeyboardDismissModeNone; i <= UIScrollViewKeyboardDismissModeInteractive; i++) {
        NSString *mode = [self scrollViewKeyboardDismissModeDescription:i];
        if (mode) {
            [modes addObject:mode];
        }
    }
    return [modes copy];
}

+ (NSString *)tableViewStyleDescription:(UITableViewStyle)style {
    switch (style) {
        case UITableViewStylePlain:
            return @"Plain";
        case UITableViewStyleGrouped:
            return @"Grouped";
#ifdef __IPHONE_13_0
        case UITableViewStyleInsetGrouped:
            return @"Inset Grouped";
#endif
    }
    return nil;
}

+ (NSString *)tableViewCellSeparatorStyleDescription:(UITableViewCellSeparatorStyle)style {
    switch (style) {
        case UITableViewCellSeparatorStyleNone:
            return @"None";
        case UITableViewCellSeparatorStyleSingleLine:
            return @"Single Line";
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
        case UITableViewCellSeparatorStyleSingleLineEtched:
            return @"Single Line Etched";
#pragma clang diagnostic pop
    }
    return nil;
}

+ (NSArray <NSString *>*)tableViewCellSeparatorStyles {
    NSMutableArray *styles = [[NSMutableArray alloc] init];
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    for (UITableViewCellSeparatorStyle i = UITableViewCellSeparatorStyleNone; i <= UITableViewCellSeparatorStyleSingleLineEtched; i++) {
        NSString *style = [self tableViewCellSeparatorStyleDescription:i];
        if (style) {
            [styles addObject:style];
        }
    }
#pragma clang diagnostic pop
    return [styles copy];
}

+ (NSString *)tableViewSeparatorInsetReferenceDescription:(UITableViewSeparatorInsetReference)reference {
    switch (reference) {
        case UITableViewSeparatorInsetFromCellEdges:
            return @"From Cell Edges";
        case UITableViewSeparatorInsetFromAutomaticInsets:
            return @"Automatic Insets";
    }
    return nil;
}

+ (NSArray <NSString *>*)tableViewSeparatorInsetReferences {
    NSMutableArray *references = [[NSMutableArray alloc] init];
    for (UITableViewSeparatorInsetReference i = UITableViewSeparatorInsetFromCellEdges; i <= UITableViewSeparatorInsetFromAutomaticInsets; i++) {
        NSString *reference = [self tableViewSeparatorInsetReferenceDescription:i];
        if (reference) {
            [references addObject:reference];
        }
    }
    return [references copy];
}

+ (NSString *)tableViewCellSelectionStyleDescription:(UITableViewCellSelectionStyle)style {
    switch (style) {
        case UITableViewCellSelectionStyleNone:
            return @"None";
        case UITableViewCellSelectionStyleBlue:
            return @"Blue";
        case UITableViewCellSelectionStyleGray:
            return @"Gray";
        case UITableViewCellSelectionStyleDefault:
            return @"Default";
    }
    return nil;
}

+ (NSArray <NSString *>*)tableViewCellSelectionStyles {
    NSMutableArray *styles = [[NSMutableArray alloc] init];
    for (UITableViewCellSelectionStyle i = UITableViewCellSelectionStyleNone; i <= UITableViewCellSelectionStyleDefault; i++) {
        NSString *style = [self tableViewCellSelectionStyleDescription:i];
        if (style) {
            [styles addObject:style];
        }
    }
    return [styles copy];
}

+ (NSString *)tableViewCellAccessoryTypeDescription:(UITableViewCellAccessoryType)type {
    switch (type) {
        case UITableViewCellAccessoryNone:
            return @"None";
        case UITableViewCellAccessoryDisclosureIndicator:
            return @"Disclosure Indicator";
        case UITableViewCellAccessoryDetailDisclosureButton:
            return @"Disclosure Button";
        case UITableViewCellAccessoryCheckmark:
            return @"Checkmark";
        case UITableViewCellAccessoryDetailButton:
            return @"Detail Button";
    }
    return nil;
}

+ (NSArray <NSString *>*)tableViewCellAccessoryTypes {
    NSMutableArray *types = [[NSMutableArray alloc] init];
    for (UITableViewCellAccessoryType i = UITableViewCellAccessoryNone; i <= UITableViewCellAccessoryDetailButton; i++) {
        NSString *type = [self tableViewCellAccessoryTypeDescription:i];
        if (type) {
            [types addObject:type];
        }
    }
    return [types copy];
}

+ (NSString *)datePickerModeDescription:(UIDatePickerMode)mode {
    switch (mode) {
        case UIDatePickerModeDate:
            return @"Date";
        case UIDatePickerModeTime:
            return @"Time";
        case UIDatePickerModeDateAndTime:
            return @"Date and Time";
        case UIDatePickerModeCountDownTimer:
            return @"Count Down Timer";
    }
    return nil;
}

+ (NSArray <NSString *>*)datePickerModes {
    NSMutableArray *modes = [[NSMutableArray alloc] init];
    for (UIDatePickerMode i = UIDatePickerModeDate; i <= UIDatePickerModeCountDownTimer; i++) {
        NSString *mode = [self datePickerModeDescription:i];
        if (mode) {
            [modes addObject:mode];
        }
    }
    return [modes copy];
}

+ (NSString *)barStyleDescription:(UIBarStyle)style {
    switch (style) {
        case UIBarStyleDefault:
            return @"Default";
        case UIBarStyleBlack:
            return @"Black";
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
        case UIBarStyleBlackTranslucent:
            return @"Black Translucent";
#pragma clang diagnostic pop
        }
    return nil;
}

+ (NSArray <NSString *>*)barStyles {
    NSMutableArray *styles = [[NSMutableArray alloc] init];
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    for (UIBarStyle i = UIBarStyleDefault; i <= UIBarStyleBlackTranslucent; i++) {
        NSString *style = [self barStyleDescription:i];
        if (style) {
            [styles addObject:style];
        }
    }
#pragma clang diagnostic pop
    return [styles copy];
}

+ (NSString *)searchBarStyleDescription:(UISearchBarStyle)style {
    switch (style) {
        case UISearchBarStyleDefault:
            return @"Default";
        case UISearchBarStyleProminent:
            return @"Prominent";
        case UISearchBarStyleMinimal:
            return @"Minimal";
    }
    return nil;
}

+ (NSArray <NSString *>*)searchBarStyles {
    NSMutableArray *styles = [[NSMutableArray alloc] init];
    for (UISearchBarStyle i = UISearchBarStyleDefault; i <= UISearchBarStyleMinimal; i++) {
        NSString *style = [self searchBarStyleDescription:i];
        if (style) {
            [styles addObject:style];
        }
    }
    return [styles copy];
}

+ (NSString *)tabBarItemPositioningDescription:(UITabBarItemPositioning)positioning {
    switch (positioning) {
        case UITabBarItemPositioningAutomatic:
            return @"Automatic";
        case UITabBarItemPositioningFill:
            return @"Fill";
        case UITabBarItemPositioningCentered:
            return @"Centered";
    }
    return nil;
}

+ (NSArray <NSString *>*)tabBarItemPositionings {
    NSMutableArray *positionings = [[NSMutableArray alloc] init];
    for (UITabBarItemPositioning i = UITabBarItemPositioningAutomatic; i <= UITabBarItemPositioningCentered; i++) {
        NSString *positioning = [self tabBarItemPositioningDescription:i];
        if (positioning) {
            [positionings addObject:positioning];
        }
    }
    return [positionings copy];
}

+ (NSString *_Nullable)layoutAttributeDescription:(NSLayoutAttribute)attribute {
    switch (attribute) {
        case NSLayoutAttributeLeft:
            return @"left";
        case NSLayoutAttributeRight:
            return @"right";
        case NSLayoutAttributeTop:
            return @"top";
        case NSLayoutAttributeBottom:
            return @"bottom";
        case NSLayoutAttributeLeading:
            return @"leading";
        case NSLayoutAttributeTrailing:
            return @"trailing";
        case NSLayoutAttributeWidth:
            return @"width";
        case NSLayoutAttributeHeight:
            return @"height";
        case NSLayoutAttributeCenterX:
            return @"centerX";
        case NSLayoutAttributeCenterY:
            return @"centerY";
        case NSLayoutAttributeLastBaseline:
            return @"lastBaseline";
        case NSLayoutAttributeFirstBaseline:
            return @"firstBaseline";
        case NSLayoutAttributeLeftMargin:
            return @"leftMargin";
        case NSLayoutAttributeRightMargin:
            return @"rightMargin";
        case NSLayoutAttributeTopMargin:
            return @"topMargin";
        case NSLayoutAttributeBottomMargin:
            return @"bottomMargin";
        case NSLayoutAttributeLeadingMargin:
            return @"leadingMargin";
        case NSLayoutAttributeTrailingMargin:
            return @"trailingMargin";
        case NSLayoutAttributeCenterXWithinMargins:
            return @"centerXMargin";
        case NSLayoutAttributeCenterYWithinMargins:
            return @"centerYMargin";
        case NSLayoutAttributeNotAnAttribute:
            return nil;
        default:
            return nil;
    }
}

+ (NSArray <NSString *>*)layoutAttributes {
    NSMutableArray *attributes = [[NSMutableArray alloc] init];
    for (NSLayoutAttribute i = NSLayoutAttributeNotAnAttribute; i <= NSLayoutAttributeCenterYWithinMargins; i++) {
        NSString *attribute = [self layoutAttributeDescription:i];
        if (attribute) {
            [attributes addObject:attribute];
        }
    }
    return [attributes copy];
}

+ (NSString *_Nullable)layoutRelationDescription:(NSLayoutRelation)relation {
    switch (relation) {
        case NSLayoutRelationLessThanOrEqual:
            return @"<=";
        case NSLayoutRelationEqual:
            return @"=";
        case NSLayoutRelationGreaterThanOrEqual:
            return @">=";
        default:
            return nil;
    }
}

@end
