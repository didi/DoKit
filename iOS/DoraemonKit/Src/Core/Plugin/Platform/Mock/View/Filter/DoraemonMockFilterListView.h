//
//  DoraemonMockDropdownListView.h
//  DoraemonKit
//
//  Created by didi on 2019/10/24.
//

#import <UIKit/UIKit.h>

@protocol DoraemonMockFilterBgroundDelegate<NSObject>

- (void)filterBgroundClick;
- (void)filterSelectedClick;

@end

@interface DoraemonMockFilterListView : UIView

@property (nonatomic, weak) id<DoraemonMockFilterBgroundDelegate> delegate;

@property (nonatomic, assign) NSInteger selectedIndex;

- (void)showList:(NSArray *)itemArray;

- (void)closeList;

@end


