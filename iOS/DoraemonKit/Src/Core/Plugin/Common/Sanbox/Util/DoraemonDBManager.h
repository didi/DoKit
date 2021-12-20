//
//  DoraemonDBManager.h
//  DoraemonKit
//
//  Created by yixiang on 2019/3/30.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonDBManager : NSObject

+ (DoraemonDBManager *)shareManager;

@property (nonatomic, copy) NSString *dbPath;
@property (nonatomic, copy) NSString *tableName;

- (NSArray *)tablesAtDB;
- (NSArray *)dataAtTable;

@end

NS_ASSUME_NONNULL_END
