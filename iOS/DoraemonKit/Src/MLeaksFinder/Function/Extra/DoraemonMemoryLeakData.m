//
//  DoraemonMemoryLeakData.m
//  DoraemonKit-DoraemonKit
//
//  Created by didi on 2019/10/7.
//

#import "DoraemonMemoryLeakData.h"
#import "NSObject+MemoryLeak.h"
#import <FBRetainCycleDetector/FBRetainCycleDetector.h>

@interface DoraemonMemoryLeakData()

@property (nonatomic, strong) NSMutableArray *dataArray;

@end

@implementation DoraemonMemoryLeakData

+ (DoraemonMemoryLeakData *)shareInstance{
    static dispatch_once_t once;
    static DoraemonMemoryLeakData *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonMemoryLeakData alloc] init];
    });
    return instance;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        _dataArray = [NSMutableArray array];
    }
    return self;
}

- (void)addObject:(id)object{
    NSString *className = NSStringFromClass([object class]);
    NSNumber *classPtr = @((uintptr_t)object);
    NSArray *viewStack = [object viewStack];
    
    NSString *retainCycle = [self getRetainCycleByObject:object];
    
    [_dataArray addObject:@{
        @"className":className,
        @"classPtr":classPtr,
        @"viewStack":viewStack,
        @"retainCycle":retainCycle
    }];
}

- (void)removeObjectPtr:(NSNumber *)objectPtr{
    for (NSInteger i=_dataArray.count-1; i == 0; i--) {
        NSDictionary *dic = _dataArray[i];
        if ([dic[@"classPtr"] isEqualToNumber:objectPtr]) {
            [_dataArray removeObjectAtIndex:i];
        }
    }
}


- (NSArray *)getResult{
    return _dataArray;
}

- (void)clearResult{
    [_dataArray removeAllObjects];
}

- (NSString *)getRetainCycleByObject:(id)object{
    NSString *result;
    
    FBRetainCycleDetector *detector = [FBRetainCycleDetector new];
    [detector addCandidate:object];
    NSSet *retainCycles = [detector findRetainCyclesWithMaxCycleLength:20];
    
    BOOL hasFound = NO;
    for (NSArray *retainCycle in retainCycles) {
        NSInteger index = 0;
        for (FBObjectiveCGraphElement *element in retainCycle) {
            if (element.object == object) {
                NSArray *shiftedRetainCycle = [self shiftArray:retainCycle toIndex:index];
                
                result = [NSString stringWithFormat:@"%@", shiftedRetainCycle];
                hasFound = YES;
                break;
            }
            
            ++index;
        }
        if (hasFound) {
            break;
        }
    }
    if (!hasFound) {
        result = @"Fail to find a retain cycle";
    }
    return result;
}

- (NSArray *)shiftArray:(NSArray *)array toIndex:(NSInteger)index {
    if (index == 0) {
        return array;
    }
    
    NSRange range = NSMakeRange(index, array.count - index);
    NSMutableArray *result = [[array subarrayWithRange:range] mutableCopy];
    [result addObjectsFromArray:[array subarrayWithRange:NSMakeRange(0, index)]];
    return result;
}

@end
