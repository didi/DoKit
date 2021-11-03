//
//  DoraemonWeexLogSearchView.h
//  DoraemonKit
//
//  Created by yixiang on 2019/5/23.
//  Copyright © 2019年 Chameleon-Team. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol DoraemonWeexLogSearchViewDelegate  <NSObject>

- (void)searchViewInputChange:(NSString *)text;

@end

@interface DoraemonWeexLogSearchView : UIView

@property (nonatomic, weak) id<DoraemonWeexLogSearchViewDelegate> delegate;

@end

