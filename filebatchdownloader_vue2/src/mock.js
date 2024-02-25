//引入mock模块
import Mock from 'mockjs';

Mock.mock('/test', { //输出数据
    'name': '@name', //随机生成姓名
    //还可以自定义其他数据
});
Mock.mock('/list', { //输出数据
    'name': '@name', //随机生成姓名
    'age|10-20': 10
    //还可以自定义其他数据
});