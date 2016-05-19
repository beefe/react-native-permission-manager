# react-native-permission-manager
android permission util

## 提供以下方法 

### getAll(callback)

callback(err,res) : 回调
* err : null
* res : 返回的联系人信息


### getCallLog(callback)

callback(err,res) : 回调
* err : null
* res : 返回的通话记录信息

## 安装及使用

#### 安装rnpm包(已安装rnpm包的请忽略本步骤)
```shell
$ npm install rnpm -g
```

#### 安装npm包
```shell
$ npm install --save react-native-permission-manager
```

#### 添加link
```shell
$ rnpm link react-native-permission-manager
```

#### 在你的JS文件中使用 
```javascript
import React, { Component } from 'react';

import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  ToastAndroid
} from 'react-native';

import PermissionManager from 'react-native-permission-manager';

class Per extends Component {

  contact(){
    PermissionManager.getAll((err,res) => {
      ToastAndroid.show(JSON.stringify(res),ToastAndroid.LONG); 
    });
  }

  calllog(){
    PermissionManager.getCallLog((err,res) => {
      ToastAndroid.show(JSON.stringify(res),ToastAndroid.LONG);
    });
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.instructions} onPress={this.contact}>
          获取联系人
        </Text>
        <Text style={styles.instructions} onPress={this.calllog}>
          获取通话记录
        </Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  instructions: {
    fontSize: 20,
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('Per', () => Per);
```
