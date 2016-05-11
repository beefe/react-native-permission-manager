//
//  BTHMapAllowTool.h
//  AdressBookAllowPower
//
//  Created by MFHJ-DZ-001-417 on 16/5/11.
//  Copyright © 2016年 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void(^bthMapBlock)(NSString *);


@interface BTHMapAllowTool : NSObject

@property(nonatomic,copy)bthMapBlock bolock;

@end
