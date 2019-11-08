//
//  DoraemonMockModelFilter.m
//  AFNetworking
//
//  Created by didi on 2019/11/7.
//

#import "DoraemonMockModelFilter.h"

@interface DoraemonMockModelFilter()

@property (nonatomic, strong)NSMutableArray *rightButtonItems;
@property (nonatomic, strong)NSMutableArray *leftButtonItems;


@end

@implementation DoraemonMockModelFilter

-(instancetype)init{
    self = [super init];
    if(self){
        _leftButtonItems = [[NSMutableArray alloc] initWithObjects:@"所以",@"默认",@"营销", nil];
        _rightButtonItems = [[NSMutableArray alloc] initWithObjects:@"所以",@"打开",@"关闭", nil];
    }
    return self;
}

- (NSArray *)getItemArray:(NSInteger)select{
    if(select==0){
        return _leftButtonItems;
    }else if(select==1){
        return _rightButtonItems;
    }else{
        return nil;
    }
}

@end
