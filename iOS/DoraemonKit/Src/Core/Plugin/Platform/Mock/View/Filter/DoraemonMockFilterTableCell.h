//
//  DoraemonMockFilterTableCell.h
//  DoraemonKit
//
//  Created by didi on 2019/10/25.
//

#import <UIKit/UIKit.h>

@interface DoraemonMockFilterTableCell : UITableViewCell

- (void)renderUIWithTitle:(NSString *)title;

- (void)selectedColor:(BOOL)selected;

@end

