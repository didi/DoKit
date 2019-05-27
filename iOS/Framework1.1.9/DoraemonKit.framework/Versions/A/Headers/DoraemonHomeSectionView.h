//
//  DoraemonHomeSectionView.h
//  AFNetworking
//
//  Created by yixiang on 2018/11/27.
//

#import <UIKit/UIKit.h>

@interface DoraemonHomeSectionView : UIView

- (void)renderUIWithData:(NSDictionary *)data;
- (void)updateUILayoutWithData:(NSDictionary *)data;

+ (CGFloat)viewHeightWithData:(NSDictionary *)data;

@end
