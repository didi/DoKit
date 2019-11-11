//
//  DoraemonMockFilterModel.m
//  AFNetworking
//
//  Created by didi on 2019/11/7.
//

#import "DoraemonMockFilterModel.h"

@interface DoraemonMockFilterModel()

@property (nonatomic, strong)NSMutableArray *uploadButtonItems;
@property (nonatomic, strong)NSMutableArray *dataButtonItems;


@end

@implementation DoraemonMockFilterModel

-(instancetype)init{
    self = [super init];
    if(self){
        _dataButtonItems = [[NSMutableArray alloc] initWithObjects:@"所以",@"默认",@"营销", nil];
        _uploadButtonItems = [[NSMutableArray alloc] initWithObjects:@"所以",@"打开",@"关闭", nil];
    }
    return self;
}

- (NSArray *)getItemArray:(NSInteger)select{
    if(select==0){
        return _dataButtonItems;
    }else if(select==1){
        return _uploadButtonItems;
    }else{
        return nil;
    }
}

@end
