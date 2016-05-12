//
//  RCTPermissionMapManger.h
//  RCTPermissionManager
//
//  Created by MFHJ-DZ-001-417 on 16/5/12.
//  Copyright © 2016年 MFHJ-DZ-001-417. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void(^bthMapBlock)(NSString *);

@interface RCTPermissionMapManger : NSObject


@property(nonatomic,copy)bthMapBlock bolock;

@end
