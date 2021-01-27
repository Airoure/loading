
[![License](https://img.shields.io/badge/License%20-Apache%202-337ab7.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![JCenter](https://api.bintray.com/packages/airoure/myrepo/library/images/download.svg)](https://bintray.com/airoure/myrepo/library/_latestVersion)
# Android自定义加载组件
这是一个自定义的加载组件，可以实现修改加载进度，修改加载完成图，错误状态和修改错误状态图。
## 使用方法
#### 1.在build.gradle中添加依赖
```
implementation 'com.zjl.loading:library:x.x.x'
```
#### 2.在xml布局中添加组件
```
<com.zjl.loading.LoadingView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
```
#### 3.设置加载成功图
通过xml中的```app:img```或者在代码中调用```setLogo```方法可以设置加载成功图，```setLogo```中参数可以为```bitmap```,```drawable```以及```url```，使用```Glide```加载网络图片。
#### 4.设置错误状态图
通过xml中的```app:error_img```可以设置错误状态图。
#### 5.设置进度
在代码中调用```setProgress```方法可以设置当前进度，设置完之后会从当前进度逐渐加到目标进度。也可以通过```getProgress```获取当前实际进度
#### 6.设置状态
loading view的状态有三种，第一种是```LoadingView.State.ERROR```，第二种是```LoadingView.State.LOADING```，第三者是```LoadingView.State.COMPLETE```，可以在代码中通过```setState```方法设置状态。
#### 7.设置进度文字大小
可以通过xml属性中的```progress_size```来设置进度文字大小，单位是sp，不设置的话，默认是64sp。
#### 8.设置加载完成监听事件
可以通过```setOnCompletListener()```来设置
#### 9.设置点击加载完成图的回调
可以通过```setOnClickListener()```来设置
#### 10.效果展示
##### 10.1.错误状态
![image](https://github.com/Airoure/loading/blob/master/screenshot/error.PNG)

##### 10.2.加载状态
![image](https://github.com/Airoure/loading/blob/master/screenshot/loading.PNG)

##### 10.3.加载结束
![image](https://github.com/Airoure/loading/blob/master/screenshot/loading_finish.PNG)
