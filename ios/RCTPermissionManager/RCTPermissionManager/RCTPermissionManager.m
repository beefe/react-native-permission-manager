//
//  RCTPermissionManager.m
//  RCTPermissionManager
//
//  Created by MFHJ-DZ-001-417 on 16/5/12.
//  Copyright © 2016年 MFHJ-DZ-001-417. All rights reserved.
//

#import "RCTPermissionManager.h"
#import <AddressBook/AddressBook.h>
#import <UIKit/UIKit.h>
#import "RCTPermissionMapManger.h"

@implementation RCTPermissionManager

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


//这里是获取通讯录里面的数据
RCT_EXPORT_METHOD(getAdressBookInfo:(RCTResponseSenderBlock)getBack){
    
    NSMutableArray *dataSource=[[NSMutableArray alloc]init];
    
    ABAddressBookRef book = ABAddressBookCreateWithOptions(NULL, NULL);
    
    // 2.获取通讯录中得所有联系人
    CFArrayRef allPeople = ABAddressBookCopyArrayOfAllPeople(book);
    
    CFIndex count = CFArrayGetCount(allPeople);
    
    // 3.打印每一个联系人额信息
    for (int i = 0; i < count; i++) {
        // 联系人列表中的每一个人都是一个ABrecordRef
        NSMutableString *str=[[NSMutableString alloc]init];
        
        ABRecordRef prople =  CFArrayGetValueAtIndex(allPeople, i);
        
        //取出当前联系人的的电话信息
        // 获取练习人得姓名
        CFStringRef lastName = ABRecordCopyValue(prople, kABPersonLastNameProperty);
        CFStringRef firstName = ABRecordCopyValue(prople, kABPersonFirstNameProperty);
        
        str=[NSString stringWithFormat:@"姓名:%@%@",firstName,lastName];
        
        // 获取联系人的电话
        // 从联系人中获取到得电话是所有的电话
        ABMultiValueRef phones =   ABRecordCopyValue(prople, kABPersonPhoneProperty);
        // 获取当前联系人总共有多少种电话
        CFIndex phoneCount = ABMultiValueGetCount(phones);
        
        for (int i = 0; i < phoneCount; i++) {
            //          CFStringRef name = ABMultiValueCopyLabelAtIndex(phones, i);
            // 从所有的电话中取出指定的电话
            CFStringRef value =  ABMultiValueCopyValueAtIndex(phones, i);
            // NSLog(@" 电话 = %@", value);
            
            NSString *phone=[NSString stringWithFormat:@"--电话%@",value];
            
            NSString *phonenumber =[str stringByAppendingString:phone];
            [dataSource addObject:phonenumber];
        }
        
    }
    
    getBack(@[[NSNull null], dataSource);
}
//这里是获取地图的允许验证
              
RCT_EXPORT_METHOD(getMapAllow:(RCTResponseSenderBlock)getBack){
        
        RCTPermissionMapManger *tool=[[RCTPermissionMapManger alloc]init];
        
        tool.bolock=^(NSString *info){
            getBack(@[[NSNull null], info]);
        };
    }
              
@end
