//
//  DoraemonEnumDescription.h
//  DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonEnumDescription : NSObject

+ (NSString *_Nullable)lineBreakModeDescription:(NSLineBreakMode)mode;

+ (NSArray <NSString *>*)lineBreaks;

+ (NSString *_Nullable)userInterfaceStyleDescription:(UIUserInterfaceStyle)style API_AVAILABLE(ios(12.0));

+ (NSString *_Nullable)userInterfaceSizeClassDescription:(UIUserInterfaceSizeClass)sizeClass;

+ (NSString *_Nullable)traitEnvironmentLayoutDirectionDescription:(UITraitEnvironmentLayoutDirection)direction API_AVAILABLE(ios(10.0));

+ (NSString *_Nullable)viewContentModeDescription:(UIViewContentMode)mode;

+ (NSArray <NSString *>*)viewContentModeDescriptions;

+ (NSString *_Nullable)textAlignmentDescription:(NSTextAlignment)textAlignment;

+ (NSArray <NSString *>*)textAlignments;

+ (NSString *_Nullable)baselineAdjustmentDescription:(UIBaselineAdjustment)baselineAdjustment;

+ (NSArray <NSString *>*)baselineAdjustments;

+ (NSString *_Nullable)controlContentVerticalAlignmentDescription:(UIControlContentVerticalAlignment)contentVerticalAlignment;

+ (NSArray <NSString *>*)controlContentVerticalAlignments;

+ (NSString *_Nullable)controlContentHorizontalAlignmentDescription:(UIControlContentHorizontalAlignment)contentHorizontalAlignment;

+ (NSArray <NSString *>*)controlContentHorizontalAlignments;

+ (NSString *_Nullable)buttonTypeDescription:(UIButtonType)buttonType;

+ (NSString *_Nullable)controlStateDescription:(UIControlState)state;

+ (NSString *_Nullable)textBorderStyleDescription:(UITextBorderStyle)style;

+ (NSArray <NSString *>*)textBorderStyles;

+ (NSString *_Nullable)textFieldViewModeDescription:(UITextFieldViewMode)mode;

+ (NSArray <NSString *>*)textFieldViewModes;

+ (NSString *_Nullable)textAutocapitalizationTypeDescription:(UITextAutocapitalizationType)type;

+ (NSArray <NSString *>*)textAutocapitalizationTypes;

+ (NSString *_Nullable)textAutocorrectionTypeDescription:(UITextAutocorrectionType)type;

+ (NSArray <NSString *>*)textAutocorrectionTypes;

+ (NSString *_Nullable)keyboardTypeDescription:(UIKeyboardType)type;

+ (NSArray <NSString *>*)keyboardTypes;

+ (NSString *_Nullable)keyboardAppearanceDescription:(UIKeyboardAppearance)appearance;

+ (NSArray <NSString *>*)keyboardAppearances;

+ (NSString *_Nullable)returnKeyTypeDescription:(UIReturnKeyType)type;

+ (NSArray <NSString *>*)returnKeyTypes;

+ (NSString *_Nullable)activityIndicatorViewStyleDescription:(UIActivityIndicatorViewStyle)style;

+ (NSArray <NSString *>*)activityIndicatorViewStyles;

+ (NSString *_Nullable)progressViewStyleDescription:(UIProgressViewStyle)style;

+ (NSArray <NSString *>*)progressViewStyles;

+ (NSString *_Nullable)scrollViewIndicatorStyleDescription:(UIScrollViewIndicatorStyle)style;

+ (NSArray <NSString *>*)scrollViewIndicatorStyles;

+ (NSString *_Nullable)scrollViewKeyboardDismissModeDescription:(UIScrollViewKeyboardDismissMode)mode;

+ (NSArray <NSString *>*)scrollViewKeyboardDismissModes;

+ (NSString *_Nullable)tableViewStyleDescription:(UITableViewStyle)style;

+ (NSString *_Nullable)tableViewCellSeparatorStyleDescription:(UITableViewCellSeparatorStyle)style;

+ (NSArray <NSString *>*)tableViewCellSeparatorStyles;

+ (NSString *_Nullable)tableViewSeparatorInsetReferenceDescription:(UITableViewSeparatorInsetReference)reference API_AVAILABLE(ios(11.0));

+ (NSArray <NSString *>*)tableViewSeparatorInsetReferences API_AVAILABLE(ios(11.0));

+ (NSString *_Nullable)tableViewCellSelectionStyleDescription:(UITableViewCellSelectionStyle)style;

+ (NSArray <NSString *>*)tableViewCellSelectionStyles;

+ (NSString *_Nullable)tableViewCellAccessoryTypeDescription:(UITableViewCellAccessoryType)type;

+ (NSArray <NSString *>*)tableViewCellAccessoryTypes;

+ (NSString *_Nullable)datePickerModeDescription:(UIDatePickerMode)mode;

+ (NSArray <NSString *>*)datePickerModes;

+ (NSString *_Nullable)barStyleDescription:(UIBarStyle)style;

+ (NSArray <NSString *>*)barStyles;

+ (NSString *_Nullable)searchBarStyleDescription:(UISearchBarStyle)style;

+ (NSArray <NSString *>*)searchBarStyles;

+ (NSString *_Nullable)tabBarItemPositioningDescription:(UITabBarItemPositioning)positioning;

+ (NSArray <NSString *>*)tabBarItemPositionings;

+ (NSString *_Nullable)layoutAttributeDescription:(NSLayoutAttribute)attribute;

+ (NSArray <NSString *>*)layoutAttributes;

+ (NSString *_Nullable)layoutRelationDescription:(NSLayoutRelation)relation;

@end

NS_ASSUME_NONNULL_END
