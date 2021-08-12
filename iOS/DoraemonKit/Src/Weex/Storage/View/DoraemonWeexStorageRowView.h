//
//  DoraemonWeexStorageRowView.h
//  DoraemonKit
//
//  Created by yixiang on 2019/5/30.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import <UIKit/UIKit.h>
@class DoraemonWeexStorageRowView;

typedef NS_ENUM(NSInteger, DoraemonWeexStorageRowViewType) {
    DoraemonWeexStorageRowViewTypeForTitle  = 0,
    DoraemonWeexStorageRowViewTypeForOne   = 1,
    DoraemonWeexStorageRowViewTypeForTwo   = 2
};

@protocol DoraemonWeexStorageRowViewDelegate <NSObject>

- (void)rowView:(DoraemonWeexStorageRowView *)rowView didLabelTaped:(UILabel *)label;

@end

@interface DoraemonWeexStorageRowView : UIView

@property(nonatomic, copy) NSArray *dataArray;

@property(nonatomic, assign) DoraemonWeexStorageRowViewType type;

@property(nonatomic, weak) id<DoraemonWeexStorageRowViewDelegate> delegate;

@end

