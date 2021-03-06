# Canoe程序设计



## 0 又一个编程语言

生活总是这般无趣，如果还找不到有点意思的东西，那么活着的意义又是什么呢。

前段时间花了大量时间构思，想要设计一条区块链出来。目前核心部分已经大致确定的，就差怎么去实现了。卡住的地方在于，智能合约怎么实现？第一步想到的，就是需要一个编程语言。已有的编程语言，几乎都能满足我想要图灵完备的要求。但有一个问题是，直接拿过来用，有点心理上抄袭的感觉，虽然说天底下什么东西不是抄的呢。其中一个比较重要的一点是，如果拿来编写智能合约，需要对代码进行入侵，其实也不对，如果虚拟机做的好，完全可以0入侵的方式运行智能合约。不过我想的更多，万一想让智能合约直接运行在物理机上呢？这种方式几乎没法对运行状态进行控制了。所以，不管怎样，一定是要编辑智能合约代码的。

既然要解析智能合约代码，那为啥不自己弄一个编程语言，加一些自己喜欢的语法糖呢。初生牛犊不怕虎啊，实际上一个新的编程语言的难度，和随便解析代码的难度不止一个数量级。但是，公链都是玩了，咋，编程语言不能玩？

最初我以为我能设计一个好的公链，比比特币以太坊都要好，但是现在我已经放弃了，现在我的想法是，不要比它们差就行了，主要是难度太大了。至于编程语言，反正都是这辈子要做的事情，我不做这个我做什么呢，那就现在做也一样，也算了却了一桩心愿了。估计会设计的很烂，也许永远都跑不起来，也许就算跑起来，也不适合编写任何应用。算了，谁在乎呢，反正我自己是不在乎的。

之前的设计算是失败了，我想要个简单的，本以为统一对象和函数会简单，但是实际上，仍然产生了理解上的困难。本来Python和javascript算是好的仿照对象，但是我无法接受它们管理上的混乱。是否存在统一的便于理解的方式设计，并且满足将来开发需要，甚至拓展任意规模的编程语言？我想要的简单，目前我无法实现。这次我要把对象和函数分开，会加一个小技巧让对象和函数能够共存。就这样，越简单越好，这是根本目的。和现有的语言像就像吧，就算是只加一个语法糖，那也算是有好玩的地方了不是吗。

来来来，第3次修改设计。

## 1 类和函数

类和函数的关系像是先有鸡还是先有蛋的问题。无解，本来嘛，这种哲学问题那么多哲学家都没解决，我想破头，也没搞出个合理的解释。所以，我不管了。

将具体事务抽象出来，是人类思维的方式，归纳与演绎嘛。

对象，目标物体，将许多目标的共同特征进行总结规律，会得到一个抽象的东西，称为类。静态的抽象称为类，行为的抽象称为函数。

```canoe
// 用花括号把内容括起来，那就是一个类
{
	name
}

// 如果里面的东西很具体 可以称为对象
{
	name: "name"
}

// 函数是行为抽象 输入-运行-输出
// 带有一层小括号修饰的说明是函数 
// :修饰的是这个函数的输出结果的性质 默认就是输出空{}嘛
// 最后的{}是函数体，函数体必须写最后而且没有:冒号
:() :{} {}
```

## 2 命名

会再次用到的东西，都应该有名字，如果连名字都没有，没法复用。

```canoe
// 类
// 一个名字 :冒号后面的东西是用来修饰前面新定义的类的
// 任何一个类默认都是继承空的:{} 要加新东西就添加额外的修饰
Person :{
	name :string
	age :int
}

// 对象 类和对象的界限不那么清晰的，差不多
person1 :Person :{
	name: "name"
	age: 18
}

// 函数
add :int :(x :int, y :int) {
	return x + y
}
add(x int, y int) int {
	return x + y
}

getAdd :{:int :(:int, :int)} :() {
	return :(x :int, y :int) :int {
		return x + y
	}
}

setAdd :(add :int :(:int, :int)) {
	this.add = add
}

sum = add(1, 2)
```

## 3 约束

```canoe
Student :Person :{
	school :string
}
// 这样来看，是天然支持多继承的
// 有个限制，继承的多个类之间不允许有冲突
```

## 4 基本类型与对象

```canoe
// 计算机世界只有数字，没有类概念
// 为了连接 计算机世界和元世界 定义6个很单纯很不做作的类
// 计算机直接处理这6种类型
// 其本身就相当于 
int8 
int16
int32
int64
float32
float64

// 比如 整数和小数 
// 数字 1 是一个 int32类的对象
// 小数 1.1 是一个 float64类的对象

age := 18
// 相当于 age :int32 = 18 

salary => 8888.8
// 相当于 salary :float64 = 8888.8 
```

## 5 模块

不可能把所有的代码都写在一个文件里，既然要分开写，得有个方式引用其他文件代码的方式。另外，其他文件中的类或函数允不允许被引用也是需要说明的。

这里定义2种范围来描述元的可见性

```canoe
:package	// 同包可见 包定义为同属一个文件夹
			// 每一个类或函数的默认级别
			// 没有子包的概念，每个文件夹就是一个包
					
:open		// 公开的 只要知道这个类或函数就可以用 
```

```canoe
│-- src  				// 所有代码文件位置
│   │-- test1           // 文件夹test1
│   │   │-- a.canoe     // 文件夹test1下的文件a
│   │   │-- b.canoe     // 文件夹test1下的文件b
│   │-- test2           // 文件夹test2
│   │   │-- a.canoe     // 文件夹test2下的文件a
│   │-- main.canoe		// src文件下的文件main

```

```canoe
// 文件夹test1下的文件a
package test1
A :package  // 默认级别 等同于 A 
B :open
```

```canoe
// 文件夹test1下的文件b
package test1
A 			// error 同包下已经有 A了
C :{
	a :A  
} 
D :open 
```

```canoe
// 文件夹test2下的文件a
package test2
A  
B :open 
```

```canoe
// 文件夹src下的文件main
// 如果不主动声明包 则默认包为 "" 即空串 没有

import test1	// 表明本文件main有一个包 test1
import test1.B  // 表明本文件main有一个类 B
				 
import test2.B  // error 已经有一个B，导入的名称不能重复
import B2 :test2.B // 别名
import {
	test1
	B2 :test2.B
}

A :test1.B  // 导入的作用就是不用写全名称了
BBB :B

B // error B作为导入名称 不可以重复
```

## 6 枚举

虽然我很不想引入许多规则，但是这样基本的还是要的。我曾想用那种类和函数统一的方式把枚举也隐式实现了，但是放弃了。

```canoe
// 一个原本是类 一旦标记枚举关键字后 就变成了枚举类了

Week :enum :{
	Monday
	...
	
}

// 要枚举的对象，不用指定类型 所有直接只写个名字的就是枚举对象
// 其他的要写约束 至少一个约束吧
```

## 7 控制流程

用很普通的控制方式吧，rust scala kotlin都太酷炫了

```canoe
x = 5

if x > 5 {
	...
}

if x > 5 {
	...
} else {
	...
}

// 必须用{}括起来

if x > 5 {

} else if {

} else {

}

match x {
	1: ... with
	2: ...
	else: ...
}
// with关键字是接着运行

// 循环

x = [1, 2, 3, 4, 5]

for item, i in x {
	// item 是每一项
	// i 是计数
}

loop {
	...
}

// 可以加标签 用于break指定循环或continue指定循环

goto1 :goto 

goto goto1
// goto 语句
```

哎，貌似也没其他特性了，先这样吧