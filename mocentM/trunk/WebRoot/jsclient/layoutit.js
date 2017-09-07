/**
 * 描 述:  Layoutit，纯css的布局工具，需要css3(即ie8就不支持了)，适用于静态有自适应满屏要求的界面布局，
 *         支持垂直和水平两个方向布局，支持固定大小和百分比布局(但不能混用)，支持自适应，可以任意嵌套，
 *         百分比的情况下永远不会出现外部滚动条，dom加载完之后即显示完整布局(带来最快的首页展示速度体验)。
 * 历 史： 2014年12月02日 创建
 * 			
 * 参 数:  selector:css选择器，字符串类型
 *         options:布局参数，内含value和vLayout|hLayout属性
 *         value：高度或者宽度，可以是具体数字、百分比字符串、字符串"auto"，顶级对象不用填写
 *         vLayout|hLayout:子项的垂直|水平布局参数，数组类型，用来描述子项的情况，元素可以是实际数字、百分比字符串、字符串"auto"，也可以是一个options
 *         
 * 使 用： 例如需要将.myframe的四个子项垂直布局，高度分别为100,200,300,余下全部，则只需Layoutit(".myframe", {vLayout:[100, {value:200}, 300, "auto"]})。    
 *         {vLayout:[120, {vLayout: [20, "auto"]}, 30]}，表示垂直布局 120、"auto"，30，中间部分再垂直布局 20,"auto" 。 
 *         这种写法等同于{vLayout:[120, {value: "auto", vLayout: [20, "auto"]}, 30]}
 *         "auto"默认撑开剩余大小，如果已有百分比超过100%，则auto这一项将被隐藏。
 * 
 * 注 意： 由于该布局工具是在整个dom树加载完成之前通过生成纯css进行布局。所以需要你在编写html节点的时候，注意以下几点：
 *  	   1：布局完成后，布局dom节点的位置不能调整，即只能用于布局相对固定的页面。
 *         2：涉及到布局的dom节点，不得设置margin，否则可能出现布局错乱或出现滚动条。
 *         2：强烈不建议在具有子布局dom节点的dom节点设置padding，该动作可能导致布局错乱或滚动条的出现。
 */
var Layoutit = (function() {

    /**
     * 数组工具类
     */
    var ArrayHelper = (function() {

        return {
            /**
             * arr中是否含有元素obj
             */
            contains : function(arr, obj) {
                for (var i = 0; i < arr.length; i++) {
                    if (obj == arr[i]) {
                        return true;
                    }
                }
                return false;
            },

            /**
             * obj在数组arr中的个数
             */
            count : function(arr, obj) {
                var count = 0;
                for (var i = 0; i < arr.length; i++) {
                    if (obj == arr[i]) {
                        count++;
                    }
                }
                return count;
            },

            /**
             * 除了exclude指向的之外，其它内容是否都满足正则表达式regexp
             * 返回值  -1:全部跟exclude一样 0：全部满足， 1：部分满足，  2：全不满足
             */
            regexp : function(arr, regexp, exclude) {
                var arr1 = new Array(); //满足
                var arr2 = new Array(); //不满足
                for (var i = 0; i < arr.length; i++) {
                    if (exclude && exclude == arr[i]) {
                        continue;
                    }
                    if (regexp.test(arr[i])) {
                        arr1.push(arr[i]);
                    } else {
                        arr2.push(arr[i]);
                    }
                }
                if (arr1.length == 0 && arr2.length == 0) {
                    return -1;
                }
                if (arr1.length == 0) {
                    return 2;
                }
                if (arr2.length == 0) {
                    return 0;
                }
                return 1;
            }
        };
    }());

    //	正则表达式，匹配百分比、数字
    var _percent = new RegExp("^\\d+%$");
    var _number = new RegExp("^\\d+$");

    /**
     * 布局项
     */
    var PageItem = function(options) {
        this.position; //位置信息
        this.size; //尺寸信息
        this.padding; //周边信息
        this.children = new Array();
        this._init(options);
    };

    /**
     * 私有初始化方法
     */
    PageItem.prototype._init = function(options) {

        var area1 = options["vLayout"] ? "top" : "left";
        var area2 = options["vLayout"] ? "bottom" : "right";
        var layout = options["vLayout"] ? "vLayout" : "hLayout";

        var padding1 = 0;
        var padding2 = 0;
        var isAuto = false;
        if (options[layout] && options[layout].length > 0) {
            _validValue(options[layout]); //校验布局参数合法性，并且对数据做一些预处理，如果不合法，将抛出异常
            for (var i = 0; i < options[layout].length; i++) {
                var item = options[layout][i];
                if (item.value == "auto") {
                    isAuto = true;
                    break;
                }
                item.area = area1;
                item.gap = padding1;
                if (padding1 === 0 && _percent.test(item.value)) {
                    padding1 = "0%";
                }
                padding1 = _sizePlus(padding1, item.value);
            }
            if (isAuto) {
                for (var j = options[layout].length - 1; j > -1; j--) {
                    var item = options[layout][j];
                    if (item.value == "auto") {
                        break;
                    }
                    item.area = area2;
                    item.gap = padding2;
                    if (padding2 === 0 && _percent.test(item.value)) {
                        padding2 = "0%";
                    }
                    padding2 = _sizePlus(padding2, item.value);
                }
            }
            for (var k = 0; k < options[layout].length; k++) {
                this.children.push(new PageItem(options[layout][k]));
            }
            this.padding = _computePadding(layout, padding1, padding2); //如果全为百分比，则此项返回空
        }
        this.position = _computePosition(options);
        this.size = _computeSize(options);
    };

    /**
     * 转css
     * @param selector
     * @returns {String}
     */
    PageItem.prototype.toCSS = function(selector) {
        var cssString = " " + selector + " {" + this.position + this.size + (this.padding || "") + " display: block; margin: 0px; overflow: hidden; } ";
        for (var i = 0; i < this.children.length; i++) {
            cssString += this.children[i].toCSS(selector + " > :nth-child(" + (i + 1) + ")");
        }
        return cssString;
    };

    /**
     * 校验布局合法性
     */
    var _validValue = function(layout) {

        var index = -1;
        var arr = new Array();
        for (var i = 0; i < layout.length; i++) {
            var item = layout[i];
            if (_isSimpleType(typeof item)) {
                var a = {};
                a.value = item;
                layout[i] = item = a;
            }
            if (!item.value || item.value == "auto") {
                arr.push("auto");
                index = i;
                item.value = "auto";
            } else {
                arr.push(item.value);
            }
        }
        var autoCount = ArrayHelper.count(arr, "auto");
        if (autoCount > 1) {
            throw "布局参数有误，同级出现多个auto(注意：未设置value，将默认作为auto处理)!";
        }
        var regexp = ArrayHelper.regexp(arr, _percent, "auto");
        if (regexp == 1) {
            throw "布局参数有误，同级百分比和固定值不可同时存在!";
        }
        if (regexp == 0 && index > -1) { //全部都是百分比
            var total = "100%";
            for (var i = 0; i < arr.length; i++) {

                if ("auto" == arr[i]) {
                    continue;
                }
                total = _sizeMinus(total, arr[i]);
            }
            
            if (total.charAt(0) == "-") {
                layout[index].value = "0%";
            } else {
                layout[index].value = total;
            }
        }
    };

    /**
     * 尺寸减法，v1-v2.    2px - 1px = 1px   20% - 10% = 10%
     */
    var _sizeMinus = function(v1, v2) {

        if (_percent.test(v1) && _percent.test(v1)) {
            return (Number(v1.replace("%", "")) - Number(v2.replace("%", ""))) + "%";
        } else if (_number.test(v1) && _number.test(v2)) {
            return v1 - v2;
        } else {
            throw "不同类型，无法相减！";
        }
    };

    /**
     * 尺寸加法，v1+v2.  1px + 2px  = 3px   10% + 20% = 30%
     */
    var _sizePlus = function(v1, v2) {

        if (_percent.test(v1) && _percent.test(v1)) {
            return (Number(v1.replace("%", "")) + Number(v2.replace("%", ""))) + "%";
        } else if (_number.test(v1) && _number.test(v2)) {
            return v1 + v2;
        } else {
            throw "不同类型，无法相加！";
        }
    };

    /**
     * 计算布局项尺寸
     */
    var _computeSize = function(options) {

        var horizontal = [ "left", "right" ];
        var vertial = [ "top", "bottom" ];
        if (!options.area) {
            return " width: 100%; height: 100%;";
        } else {
            if (ArrayHelper.contains(horizontal, options.area)) {
                if (_percent.test(options.value)) {
                    return " width: " + options.value + "; height: 100%;";
                }
                return " width: " + options.value + "px; height: 100%;";
            } else if (ArrayHelper.contains(vertial, options.area)) {
                if (_percent.test(options.value)) {
                    return " width: 100%; height: " + options.value + ";";
                }
                return " width: 100%; height: " + options.value + "px;";
            }
        }
    };

    /**
     * 计算布局项方位
     */
    var _computePosition = function(options) {

        var horizontal = [ "left", "right" ];
        var vertial = [ "top", "bottom" ];
        if (!options.area) {
            return " position: relative;";
        } else {
            if (ArrayHelper.contains(horizontal, options.area)) {
                if (_percent.test(options.gap)) {
                    return " position: absolute; " + options.area + ": " + options.gap + "; top: 0px;";
                }
                return " position: absolute; " + options.area + ": " + options.gap + "px; top: 0px;";
            } else if (ArrayHelper.contains(vertial, options.area)) {
                if (_percent.test(options.gap)) {
                    return " position: absolute; " + options.area + ": " + options.gap + "; left: 0px;";
                }
                return " position: absolute; " + options.area + ": " + options.gap + "px; left: 0px;";
            }
        }
    };

    /**
     * 计算布局项padding
     */
    var _computePadding = function(direction, padding1, padding2) {

        if (_percent.test(padding1) || _percent.test(padding2)) {
            return null;
        }
        if (direction == "vLayout") {
            return " padding: " + padding1 + "px 0px " + padding2 + "px 0px;";
        } else {
            return " padding: 0px " + padding2 + "px 0px " + padding1 + "px;";
        }
    };

    /**
     * 是否简单类型（number，string）
     */
    var _isSimpleType = function(typeName) {

        if (typeName == "string" || typeName == "number") {
            return true;
        }
        return false;
    };

    return function(selector, options) {

        var item = new PageItem(options);
        var cssString = item.toCSS(selector);
        var globalCSS = "* { -webkit-box-sizing: border-box; -moz-box-sizing: border-box; box-sizing: border-box; } " + "html,body { padding: 0px; margin: 0px; height: 100%; }";
        var finalCSS = globalCSS + cssString;
        var head = document.getElementsByTagName('head').item(0);
        var css = document.createElement('style');
        css.type = 'text/css';
        css.textContent = finalCSS;
        head.appendChild(css);
    };

}());
