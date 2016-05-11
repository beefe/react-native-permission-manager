//
//  BTHAdressWorkMapAllow.m
//  BTHAdressWorkMapAllow
//
//  Created by MFHJ-DZ-001-417 on 16/5/11.
//  Copyright © 2016年 MFHJ-DZ-001-417. All rights reserved.
//

#import "BTHAdressWorkMapAllow.h"
#import <AddressBook/AddressBook.h>
#import <CoreLocation/CoreLocation.h>
#import <UIKit/UIKit.h>
#import "BTHMapAllowTool.h"


@implementation BTHAdressWorkMapAllow

RCT_EXPORT_MODULE();


//这里是获取通讯录的允许验证
RCT_EXPORT_METHOD(getAdressBookAllow:(RCTResponseSenderBlock)getBack){
    
    ABAddressBookRef book = ABAddressBookCreateWithOptions(NULL, NULL);
    
    ABAddressBookRequestAccessWithCompletion(book, ^(bool granted, CFErrorRef error) {
        // granted YES 代表用户授权成功 NO 代表用户授权失败
        if (granted) {
            
            NSLog(@"授权成功");
            getBack(@[[NSNull null], @"通讯录授权成功"]);
        }else
        {
            NSLog(@"授权失败");
            getBack(@[[NSNull null], @"通讯录授权失败"]);
        }
    });
}

//这里是获取地图的允许验证
RCT_EXPORT_METHOD(getMapAllow:(RCTResponseSenderBlock)getBack){
    
    BTHMapAllowTool *tool=[[BTHMapAllowTool alloc]init];
    
    tool.bolock=^(NSString *info){
        getBack(@[[NSNull null], info]);
    };
}
@end
