//
//  DoraemonMockDetailModel.h
//  AFNetworking
//
//  Created by didi on 2019/10/31.
//

#import <UIKit/UIKit.h>

@interface DoraemonMockDetailModel : NSObject

@property (nonatomic, copy) NSMutableDictionary *content;
@property (nonatomic, assign) NSTimeInterval timeInterval;
@property (nonatomic, assign) BOOL expand;
@property (nonatomic, assign) NSInteger tabBarIndex;//0\1,分别

- (DoraemonMockDetailModel *)packageArrayToModel:(NSArray *)info;

@end

