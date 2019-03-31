//
//  DoraemonDBManager.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2019/3/30.
//

#import "DoraemonDBManager.h"
#import <sqlite3.h>

@interface DoraemonDBManager()



@end

@implementation DoraemonDBManager

+ (DoraemonDBManager *)shareManager{
    static dispatch_once_t once;
    static DoraemonDBManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonDBManager alloc] init];
    });
    return instance;
}

//打开数据库
- (sqlite3 *)openDB{
    sqlite3 *db = nil;
    sqlite3_open([self.dbPath UTF8String], &db);
    return db;
}

//获取指定路径数据库中的数据表
- (NSArray *)tablesAtDB{
    sqlite3 *db = [self openDB];
    if (db == nil) {
        return nil;
    }
    NSMutableArray *tableNameArray = [NSMutableArray array];
    //查询sqlite_master表
    NSString *sql = @"select type, tbl_name from sqlite_master";
    sqlite3_stmt *stmt = NULL;
    if (sqlite3_prepare_v2(db, sql.UTF8String, -1, &stmt, NULL) == SQLITE_OK) {
        while (sqlite3_step(stmt) == SQLITE_ROW) {
            const unsigned char *type_c = sqlite3_column_text(stmt, 0);
            const unsigned char *tbl_name_c = sqlite3_column_text(stmt, 1);
            NSString *type = [NSString stringWithUTF8String:type_c];
            NSString *tbl_name = [NSString stringWithUTF8String:tbl_name_c];
            if (type && [type isEqualToString:@"table"]) {
                [tableNameArray addObject:tbl_name];
            }
        }
    }
    return tableNameArray;
}



@end
