//
//  DoraemMultiCaseModel.h
//  DoraemonKit
//
//  Created by wzp on 2021/10/11.
//

#import <Foundation/Foundation.h>
#import <JSONModel/JSONModel.h>
NS_ASSUME_NONNULL_BEGIN

@interface DoraemMultiCaseListModel : JSONModel

@property (nonatomic, strong)NSString * _id;
@property (nonatomic, strong)NSString * pId;
@property (nonatomic, strong)NSString * appName;
@property (nonatomic, strong)NSString * appVersion;
@property (nonatomic, strong)NSString * dokitVersion;
@property (nonatomic, strong)NSString * phoneMode;
@property (nonatomic, strong)NSString * systemVersion;
@property (nonatomic, strong)NSString * time;
@property (nonatomic, strong)NSString * caseId;
@property (nonatomic, strong)NSString * createTime;
@property (nonatomic, strong)NSString * caseStatus;
@property (nonatomic, strong)NSString * createDate;
@property (nonatomic, strong)NSString * caseName;
@property (nonatomic, strong)NSString * modifyTime;
@property (nonatomic, strong)NSString * personName;

//"curStatus":{
//    "status":"new",
//    "id":"new",
//    "date":1628596243293
//},
//"statusList":[
//    {
//        "status":"new",
//        "id":"new",
//        "date":1628596243293
//    }
//],
@end

NS_ASSUME_NONNULL_END
