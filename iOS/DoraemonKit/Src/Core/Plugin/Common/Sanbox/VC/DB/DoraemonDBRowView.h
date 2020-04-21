//
//  DoraemonDBRowView.h
//  AFNetworking
//
//  Created by yixiang on 2019/4/1.
//

#import <UIKit/UIKit.h>
@class DoraemonDBRowView;

typedef NS_ENUM(NSInteger, DoraemonDBRowViewType) {
    DoraemonDBRowViewTypeForTitle  = 0,
    DoraemonDBRowViewTypeForOne   = 1,
    DoraemonDBRowViewTypeForTwo   = 2
    
};

@protocol DoraemonDBRowViewTypeDelegate <NSObject>

- (void)rowView:(DoraemonDBRowView *)rowView didLabelTaped:(UILabel *)label;

@end


@interface DoraemonDBRowView : UIView

@property(nonatomic, copy) NSArray *dataArray;

@property(nonatomic, assign) DoraemonDBRowViewType type;

@property(nonatomic, weak) id<DoraemonDBRowViewTypeDelegate> delegate;

@end

