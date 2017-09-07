/*************************
 *作者：周磊
 *时间：2015.8.28
 *描述：摩森讯业基础脚本
 */
Function.__typeName = 'Function';
Object.__typeName = 'Object';
Boolean.__typeName = 'Boolean';
Date.__typeName = 'Date';
Number.__typeName = 'Number';
RegExp.__typeName = 'RegExp';
Array.__typeName = 'Array';
String.__typeName = 'String';
Error.__typeName = 'Error';

if (!window) {
    this.window = this;
}

window.mocent = Function;

var mocent = {};

//navigator type
var __navigator = navigator.userAgent.toLowerCase();
var IS_SAFARI = __navigator.indexOf("safari") > -1;
var IS_MAC = __navigator.indexOf("macintosh") > -1;
var IS_IE = __navigator.indexOf("msie") > -1 || __navigator.indexOf("trident") > -1;
var IS_WINDOWS = __navigator.indexOf("windows") > -1;
var IS_IE6 = (IS_IE && (__navigator.indexOf("msie 6") > -1));
var IS_IE7 = (IS_IE && (__navigator.indexOf("msie 7") > -1));
var IS_IE8 = (IS_IE && (__navigator.indexOf("msie 8") > -1));
var IS_IE11_PLUS = __navigator.indexOf("trident") > -1 && __navigator.indexOf("msie") < 0;
//global var===================================================================

mocent.isDebug = false;

mocent.baseUrl = null;
mocent.baseThemeUrl = null;

mocent.appPath = "";
mocent.appType = 1;

//private var============================================================================================
mocent.__loadedUrls = [];
mocent.__queryCollection = null;
mocent.__webRoot = null;
//eval javasript===============================================================
mocent.eval = function (code) {
	if (IS_IE) {
        if (IS_IE11_PLUS) {
            window.eval(code);
        }
        else {
            execScript(code);
        }
    } else {
        window.eval(code);
    }
};
//extend object===============================================================
mocent.extend = function (destination, source, props) {

    if (destination == null || source == null)
        return destination;

    if (props) {
        for (var i = 0; i < props.length; i++) {
            destination[props[i]] = source[props[i]];
        }
    }
    else {
        for (var property in source) {
            //safari event not to setter
            //if (!(IS_SAFARI && property == "event"))
            destination[property] = source[property];
        }
    }
    return destination;
};
//get base.js url==========================================================
mocent.getBaseUrl = function () {
    if (document && document.getElementsByTagName) {
        var scripts = document.getElementsByTagName("script");
        var rePkg = /base\.js(\W|$)/i;
        for (var i = 0; i < scripts.length; i++) {
            var src = scripts[i].getAttribute("src");
            if (!src) { continue; }
            var m = src.match(rePkg);
            if (m) {
                mocent.baseUrl = src.substring(0, m.index);
                mocent.appPath = scripts[i].getAttribute("apppath");
                mocent.appPath = mocent.appPath ? mocent.appPath : "";
                mocent.appType = scripts[i].getAttribute("apptype");
                mocent.appType = mocent.appType ? parseInt(mocent.appType) : 1;
                break;
            }
        }
    }
};

//register namespace======================================================================================
mocent.registerNamespace = function(namespacePath) {
	var rootObject = window;
    var namespaceParts = namespacePath.split('.');

    for (var i = 0; i < namespaceParts.length; i++) 
    {
        var currentPart = namespaceParts[i];
        var ns = rootObject[currentPart];
        if (!ns) {
            rootObject[currentPart] = new Object();
            ns = rootObject[currentPart];
        }
        rootObject = ns;
    }
}
//将url添加至已加载中
mocent.addLoadedUri = function (/*string*/uri) {
    var len = mocent.__loadedUrls.length;
    for (var i = 0; i < len; i++) {
        if (mocent.__loadedUrls[i] == uri) {
            return;
        }
    }
    mocent.__loadedUrls.push(uri);
};
//获取加载的全路径
mocent.getloadUri = function (/*String*/moduleName, /*0,1*/resourceType) {
	var relpath = moduleName;//.split(".").join("/");
    var uri = "";
    var path = (resourceType == 1 ? mocent.baseThemeUrl : mocent.baseUrl);
    if (relpath.charAt(0) == '/' || relpath.match(/^\w+:/)) {
        path = "";
    }
    else if (relpath.charAt(0) == '#') {
        relpath = relpath.substr(1);
        path = mocent.appPath + (resourceType == 1 ? "style/" : "javascript/");
    }

    uri = path + relpath + (resourceType == 1 ? '.css' : '.js');

    return uri;//.toLowerCase();
};
//=============================================================================
//mocent.Url
//=============================================================================
mocent.Url = function (url) {
	this.url = url ? url : new String(window.location.toString());
};

mocent.Url.prototype.getUrl = function () {
	return this.url;
};

mocent.Url.prototype.removeSearch = function (/*string*/key) {
	 if (key == null && key == "") {
	     return this.url;
	 }
	 var tempItems = this.getQueryItems();
	 var search = "";
	 for (var i = 0; i < tempItems.length; i++) {
	     if (tempItems[i].name != key) {
	         search += "&" + tempItems[i].name + "=" + escape(tempItems[i].value);
	     }
	 }
	 search = (search.length > 0) ? "?" + search.substring(1) : "";
	 this.url = this.getNoQueryUrl() + search;
	 return this.url;
};

mocent.Url.prototype.getQueryItems = function () {
	 var tempQItems = [];
	
	 var queryString = this.getQueryString();
	 if (queryString == null || queryString == "") {
	     return tempQItems;
	 }
	
	 var tempArr = queryString.split("&");
	 for (i = 0; i < tempArr.length; i++) {
	     if (tempArr[i] == "") {
	         continue;
	     }
	     var temp = tempArr[i].split("=");
	     var str1 = temp[0];
	     var str2 = unescape(temp[1]);
	     tempQItems.push({ "name": str1, "value": str2 });
	 }
	 return tempQItems;
};

mocent.Url.prototype.getQueryString = function () {
	 var tempArr = this.url.split("\?");
	 if (tempArr && tempArr.length == 2) {
	     return tempArr[1];
	 }
	 return "";
	};
	mocent.Url.prototype.getNoQueryUrl = function () {
	 var tempArr = this.url.split("\?");
	 return tempArr[0];
};

/**
url查询字符串
test.htm?id=5; 
mocent.queryString("id")=="5"
mocent.queryString("other")==""
*/
mocent.queryString = function (/*string*/name) {
 if (mocent.__queryCollection == null) {
	 mocent.__queryCollection = new mocent.Url().getQueryItems();
     return "";
 }
 if (name == null) {
     return "";
 }
 for (var i = 0; i < mocent.__queryCollection.length; i++) {
     if (name.equal(mocent.__queryCollection[i].name)) {
         return mocent.__queryCollection[i].value;
     }
 }
 return "";
};
//============================================
mocent.registerNamespace("mocent.application");

//兼容大小写
mocent.Application = mocent.application;

mocent.getWebRoot = function () {
    if (mocent.__webRoot == null) {
    	mocent.__webRoot = mocent.appPath + "../../";
    }
    return mocent.__webRoot;
};
//=============================================================================
//非IE浏览器的增强,在使用非IE浏览器的时候自动加载
//=============================================================================
if (!IS_IE) {
  Element.prototype.getXml = function (node) {
      var oNode = (node == null) ? this : node;
      if (this.ownerDocument.getXml) {
          return this.ownerDocument.getXml(oNode);
      }
      else { throw "For XML Elements Only"; }
  };
  Element.prototype.getText = function (node) {
      var oNode = (node == null) ? this : node;
      if (this.ownerDocument.getText) {
          return this.ownerDocument.getText(oNode);
      }
      else { throw "For XML Elements Only"; }
  };
  // prototying the Element
  Element.prototype.selectNodes = function (cXPathString) {
      if (this.ownerDocument.selectNodes) {
          return this.ownerDocument.selectNodes(cXPathString, this);
      }
      else { throw "For XML Elements Only"; }
  };
  // prototying the Element
  Element.prototype.selectSingleNode = function (cXPathString) {
      if (this.ownerDocument.selectSingleNode) {
          return this.ownerDocument.selectSingleNode(cXPathString, this);
      }
      else { throw "For XML Elements Only"; }
  };
  Document.prototype.__defineGetter__("xml", function () {
      return ((new XMLSerializer()).serializeToString(this));
  });
  // xml text
  Element.prototype.__defineGetter__("text", function () {
      return this.textContent;
  });
  Element.prototype.__defineSetter__("text", function (sValue) {
      if (this.firstChild && (this.firstChild.nodeType == 3 || this.firstChild.nodeType == 4)) {
          this.firstChild.nodeValue = sValue;
      } else if (!this.firstChild) {
          var _textNode = this.ownerDocument.createTextNode(sValue);
          this.appendChild(_textNode);
      }
  });
  Element.prototype.__defineGetter__("xml", function () {
      if (this.nodeType == 9) {
          return ((new XMLSerializer()).serializeToString(this));
      } else {
          var doc = (new DOMParser()).parseFromString('<tId_df_FGD_dop/>', "text/xml");
          doc.documentElement.appendChild(this.cloneNode(true));
          var re = ((new XMLSerializer()).serializeToString(doc));
          re = re.replace('<tId_df_FGD_dop>', '').replace('</tId_df_FGD_dop>', '');
          return re;
      }
  });

  if (window.HTMLElement) {
      HTMLElement.prototype.__defineSetter__("outerHTML", function (sHTML) {
          var r = this.ownerDocument.createRange();
          r.setStartBefore(this);
          var df = r.createContextualFragment(sHTML);
          this.parentNode.replaceChild(df, this);
          return sHTML;
      });

      HTMLElement.prototype.__defineGetter__("outerHTML", function () {
          var attr;
          var attrs = this.attributes;
          var str = "<" + this.tagName.toLowerCase();
          for (var i = 0; i < attrs.length; i++) {
              attr = attrs[i];
              if (attr.specified)
                  str += " " + attr.name + '="' + attr.value + '"';
          }
          if (!this.canHaveChildren)
              return str + ">";
          return str + ">" + this.innerHTML + "</" + this.tagName.toLowerCase() + ">";
      });

      HTMLElement.prototype.__defineGetter__("canHaveChildren", function () {
          switch (this.tagName.toLowerCase()) {
              case "area":
              case "base":
              case "basefont":
              case "col":
              case "frame":
              case "hr":
              case "img":
              case "br":
              case "input":
              case "isindex":
              case "link":
              case "meta":
              case "param":
                  return false;
          }
          return true;

      });
      HTMLElement.prototype.__defineGetter__("innerText",
              function () {
                  var anyString = "";
                  var childS = this.childNodes;
                  for (var i = 0; i < childS.length; i++) {
                      if (childS[i].nodeType == 1)
                          anyString += childS[i].tagName == "BR" ? '\n' : childS[i].innerText;
                      else if (childS[i].nodeType == 3)
                          anyString += childS[i].nodeValue;
                  }
                  return anyString;
              }
          );
      HTMLElement.prototype.__defineSetter__("innerText",
              function (sText) {
                  this.textContent = sText;
              }
          );
      HTMLElement.prototype.fireEvent = function (sEventName) {
          var e = document.createEvent("HTMLEvents");
          e.initEvent(sEventName.toString().replace(/on/, ""), false, false);
          this.dispatchEvent(e);
      };

      //2012-06-05
      //window.constructor.prototype.__defineGetter__("event", mocent.getEvent);

      Event.prototype.__defineSetter__("returnValue", function (b) {
          if (!b) this.preventDefault();
          return b;
      });
      Event.prototype.__defineSetter__("cancelBubble", function (b) {
          if (b) this.stopPropagation();
          return b;
      });
      Event.prototype.__defineGetter__("srcElement", function () {
          var node = this.target;
          while (node.nodeType != 1) node = node.parentNode;
          return node;
      });
      Event.prototype.__defineGetter__("fromElement", function () {
          var node;
          if (this.type == "mouseover")
              node = this.relatedTarget;
          else if (this.type == "mouseout")
              node = this.target;
          if (!node) return;
          while (node.nodeType != 1) node = node.parentNode;
          return node;
      });
      Event.prototype.__defineGetter__("toElement", function () {
          var node;
          if (this.type == "mouseout")
              node = this.relatedTarget;
          else if (this.type == "mouseover")
              node = this.target;
          if (!node) return;
          while (node.nodeType != 1) node = node.parentNode;
          return node;
      });
      Event.prototype.__defineGetter__("offsetX", function () {
          return this.layerX;
      });
      Event.prototype.__defineGetter__("offsetY", function () {
          return this.layerY;
      });
  }
	  /**
	  2012-06-05
	  Object.prototype.transformNode = function (sXsltObj) {
	  var xslt = new XSLTProcessor();
	  xslt.importStylesheet(sXsltObj);
	  var outerHTMLObj = xslt.transformToFragment(this, document);
	  var divEl = document.createElement("div");
	  divEl.appendChild(outerHTMLObj);
	  var outerHTML = divEl.innerHTML;
	  return outerHTML;
	  };
	  
	  Object.prototype.attachEvent = function (sEventName, sFuncitonName) {
	  this.addEventListener(sEventName.toString().replace(/on/, ""), sFuncitonName, false);
	  };    
	
	  Object.prototype.selectNodes = function (sExpr) {
	  if (typeof XPathEvaluator == "undefined") {
	  throw "selectNodes not defined";
	  }
	
	  var xpe = new XPathEvaluator();
	  var ownerDocument = this.ownerDocument == null ? this.documentElement : this.ownerDocument.documentElement;
	
	  try {
	  var nsResolver = xpe.createNSResolver(ownerDocument);
	  var result = xpe.evaluate(sExpr, this, nsResolver, 0, null);
	  }
	  catch (ex) {
	  return ["there is an error when using xpath "];
	  }
	
	  var found = [];
	  while (res = result.iterateNext())
	  found.push(res);
	
	  return found;
	  };
	  Object.prototype.selectSingleNode = function (sExpr) {
	  var obj = this.selectNodes(sExpr);
	  if (obj && obj.length > 0)
	  return obj[0];
	  else
	  return null;
	  };
	  
	  Object.prototype.setProperty = function () { };
	  */
}
/**
引用脚本
moduleName可以为字符串，字符数组
实际上在未来的版本中可不对外提供，因组件无需引用，由框架自身来判断加载脚本
*/
mocent.require = function (/*String*/moduleName, isUrl) {
	if (typeof moduleName == "object") {
        for (var i = 0; i < moduleName.length; i++) {
            mocent.require(moduleName[i], isUrl);
        }
        return;
    }
    if (moduleName == "") {
        return;
    }
    var uri = isUrl ? moduleName : mocent.getloadUri(moduleName, 0);
    for (var i = 0; i < mocent.__loadedUrls.length; i++) {
        if (mocent.__loadedUrls[i] == uri) {
            return;
        }
    }

    jQuery.ajax(uri, { async: false,
        success: function (result) {
            if (mocent.util.isNullOrEmpty(result) || result.charAt(0) == "<") {
                return;
            }
            try { 
            	mocent.eval(result); 
            }
            catch (e) { alert(e); }
        },
        dataType: "text", mimeType: "text/xml", error: function (obj, ex) { throw ex; }
    });

    mocent.addLoadedUri(uri);
};
//=============================================================================
//mocent.XmlDoc
//=============================================================================
mocent.XmlDoc = function () {
	this.__typeName = "mocent.XmlDoc";
  var tXmlDoc = {};
  if (window.ActiveXObject !== undefined) {
      var tMsXmlDomType = ["Msxml2.DOMDocument.6.0", "Msxml2.DOMDocument.5.0", "Msxml2.DOMDocument.4.0", "Msxml2.DOMDocument.3.0", "MSXML2.DOMDocument", "MSXML.DOMDocument", "Microsoft.XMLDOM"];
      for (var i = 0; i < tMsXmlDomType.length; i++) {
          try {
              tXmlDoc = new ActiveXObject(tMsXmlDomType[i]);
              tXmlDoc.setProperty("SelectionLanguage", "XPath");
              break;
          }
          catch (ex) { }
      }
  }
  else if (document.implementation && document.implementation.createDocument) {
      tXmlDoc = document.implementation.createDocument("", "", null);
      // for safari,opera
      if (!tXmlDoc.load) {
          tXmlDoc.async = true;
          tXmlDoc.hp = new XMLHttpRequest();
          if (tXmlDoc.hp.overrideMimeType) {
              tXmlDoc.hp.overrideMimeType("text/xml"); //Mozilla
          }
          tXmlDoc.onload = function () { };
          tXmlDoc.hp.hpOwner = tXmlDoc;

          tXmlDoc.hp.load = function (sUrl) {
              if (this.hpOwner.async) {
                  this.hpOwner.hp.onreadystatechange = function () {
                      if (this.readyState == 4) {
                          var result = this.responseXML;

                          while (this.hpOwner.hasChildNodes()) {
                              this.hpOwner.removeChild(this.hpOwner.lastChild);
                          }
                          for (var i = 0; i < result.childNodes.length; i++) {
                              this.hpOwner.appendChild(this.hpOwner.importNode(result.childNodes[i], true));
                          }
                          this.hpOwner.onload = 1; alert(this.hpOwner.onload);
                          if (this.hpOwner.onload)	   //safari no onload   
                              this.hpOwner.onload(result);
                      }
                  };
              }
              try {
                  this.open("GET", sUrl, this.hpOwner.async);
                  this.send(null);
              }
              catch (ex) {
                  //throw "you can't connect the page using different domain,if you not config the Server-Side code";
              }
              if (!this.hpOwner.async) {
                  var result = this.responseXML;

                  while (this.hpOwner.hasChildNodes()) {
                      this.hpOwner.removeChild(this.hpOwner.lastChild);
                  }
                  for (var i = 0; i < result.childNodes.length; i++) {
                      this.hpOwner.appendChild(this.hpOwner.importNode(result.childNodes[i], true));
                  }
              }
          };
          tXmlDoc.load = function (sUrl) {
              tXmlDoc.hp.load(sUrl);
          };
      }
      // apply to ie
      tXmlDoc.onreadystatechange = function () { };
      //tXmlDoc.readyState = 4;  // because other browers not this property
      tXmlDoc.onload = function (s) {
          if (tXmlDoc.hp) {
              var result = s;
              while (this.hasChildNodes()) {
                  this.removeChild(this.lastChild);
              }
              for (var i = 0; i < result.childNodes.length; i++) {
                  this.appendChild(this.importNode(result.childNodes[i], true));
              }
          }
          this.readyState = 4;
          this.onreadystatechange(s);
      };

      tXmlDoc.getXml = function (node) {
          var oNode = (node == null) ? this : node;
          if (IS_IE) {
              return oNode.xml;
          } else {
              var oSerializer = new XMLSerializer();
              return oSerializer.serializeToString(oNode);
          }
      };
      tXmlDoc.getText = function (node) {
          var oNode = (node == null) ? this : node;
          if (IS_IE) {
              return oNode.text;
          } else {
              var sText = "";
              for (var i = 0; i < oNode.childNodes.length; i++) {
                  if (oNode.childNodes[i].hasChildNodes()) {
                      sText += this.getText(oNode.childNodes[i]);
                  } else {
                      sText += oNode.childNodes[i].nodeValue;
                  }
              }
              return sText;
          }
      };
      if (document.implementation.hasFeature("XPath", "3.0")) {
          // prototying the XMLDocument
          tXmlDoc.selectNodes = function (cXPathString, xNode) {
              if (!xNode) { xNode = this; }
              var oNSResolver = this.createNSResolver(this.documentElement);
              var aItems = this.evaluate(cXPathString, xNode, oNSResolver,
                         XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
              var aResult = [];
              for (var i = 0; i < aItems.snapshotLength; i++) {
                  aResult[i] = aItems.snapshotItem(i);
              }
              return aResult;
          };
          // prototying the XMLDocument
          tXmlDoc.selectSingleNode = function (cXPathString, xNode) {
              if (!xNode) { xNode = this; }
              var xItems = this.selectNodes(cXPathString, xNode);
              if (xItems.length > 0) {
                  return xItems[0];
              } else {
                  return null;
              }
          };
      };
      //apply load xml string funciton
      tXmlDoc.loadXML = function (s) {
          var doc2 = (new DOMParser()).parseFromString(s, "text/xml");
          var roottag = doc2.documentElement;
          if ((roottag.tagName == "parserError") || (roottag.namespaceURI == "http://www.mozilla.org/newlayout/xml/parsererror.xml")) {
              var sourceText = roottag.lastChild.firstChild.nodeValue;
              throw ("load XML by string happen error,error detail:\n" + roottag.firstChild.nodeValue + "\n" + sourceText);
              return false;
          }
          while (this.hasChildNodes()) {
              this.removeChild(this.lastChild);
          }
          for (var i = 0; i < doc2.childNodes.length; i++) {
              this.appendChild(this.importNode(doc2.childNodes[i], true));
          }
          this.onreadystatechange();
      };
  }
  if (!tXmlDoc) {
      throw ("Can not create xml doc object!");
  }

  if (!IS_IE) {
      tXmlDoc.transformNode = function (sXsltObj) {
          var xslt = new XSLTProcessor();
          xslt.importStylesheet(sXsltObj);
          var outerHTMLObj = xslt.transformToFragment(this, document);
          var divEl = document.createElement("div");
          divEl.appendChild(outerHTMLObj);
          var outerHTML = divEl.innerHTML;
          return outerHTML;
      };
      tXmlDoc.setProperty = function () { };
  }
  return tXmlDoc;
};
//=============================================================================
//mocent.util
//=============================================================================	 
mocent.registerNamespace("mocent.util");
//去除前后空格
mocent.util.trim = function (str) {
    if (str == null)
        return "";

    str = String(str);
    var reg = /^\s*/;
    str = str.replace(reg, "");
    reg = /\s*$/;
    str = str.replace(reg, "");
    return str;
};
//去除后空格
mocent.util.trimEnd = function (str, reg) {
    if (str == null)
        return "";

    str = String(str);
    reg = reg ? reg : "\s";
    var newReg = new RegExp("" + reg + "$", "i");
    str = str.replace(newReg, "");
    return str;
};
//去除前空格
mocent.util.trimStart = function (str, reg) {
    if (str == null)
        return "";

    reg = reg ? reg : "\s";
    str = String(str);
    var newReg = new RegExp("^" + reg + "", "i");
    str = str.replace(newReg, "");
    return str;
};
//是否空或""
mocent.util.isNullOrEmpty = function (str) {
    return mocent.util.trim(str) == "" ? true : false;
};
//填充左边
mocent.util.padLeft = function (str, len, c) {
    var padChar = (c && c.length > 0) ? c.charAt(0) : " ";
    var resultVal = new String(str);
    while (resultVal.length < len) {
        resultVal = padChar + resultVal;
    }
    return resultVal;
};
/**
* @example mocent.util.cookie('the_cookie', 'the_value', { expires: 7, path: '/', domain: 'mocent.com', secure: true });
* @example mocent.util.cookie('the_cookie', 'the_value');
* @example mocent.util.cookie('the_cookie', null); 
* @example mocent.util.cookie('the_cookie');
*/
mocent.util.cookie = function (name, value, options) {
	
    if (typeof value != 'undefined') { // name and value given, set cookie
        options = mocent.extend({ path: "/" }, options);
        if (value === null || value == "") {
            value = '';
            options.expires = -1;
        }
        var expires = '';
        if (options.expires && (typeof options.expires == 'number' || options.expires.toGMTString)) {
            var date;
            if (typeof options.expires == 'number') {
                date = new Date();
                date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
            } else {
                date = options.expires;
            }
            expires = '; expires=' + date.toGMTString(); // use expires attribute, max-age is not supported by IE            
        }

        var path = options.path ? '; path=' + (options.path) : '';
        var domain = options.domain ? '; domain=' + (options.domain) : '';
        var secure = options.secure ? '; secure' : '';
        document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
    } 
    else { // only name given, get cookie
        var cookieValue = null;
        if (document.cookie && document.cookie != '') {
            var cookies = document.cookie.split(';');
            for (var i = 0; i < cookies.length; i++) {
                var cookie = mocent.util.trim(cookies[i]);
                // Does this cookie string begin with the name we want?
                if (cookie.substring(0, name.length + 1) == (name + '=')) {
                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return cookieValue ? cookieValue : "";
    }
};

/**
 * extend Date Object
 */
Date.prototype.Format = function(fmt) 
{ //author: meizz 
	var o = { 
			"M+" : this.getMonth()+1,                 //月份 
			"d+" : this.getDate(),                    //日 
			"H+" : this.getHours(),                   //小时 
			"m+" : this.getMinutes(),                 //分 
			"s+" : this.getSeconds(),                 //秒 
			"q+" : Math.floor((this.getMonth()+3)/3), //季度 
			"S"  : this.getMilliseconds()             //毫秒 
	}; 
	if(/(y+)/.test(fmt))
	{
		fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
		for(var k in o)
		{
			if(new RegExp("("+ k +")").test(fmt)) 
				fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
		}
			
		return fmt;
	}
}

Date.prototype.toDateString = function (bCh) {
    var month = this.getMonth() + 1;
    var day = this.getDate();
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    return mocent.util.padLeft(this.getFullYear(), 4, '0') + (bCh ? '年' : '-') + month + (bCh ? '月' : '-') + day + (bCh ? '日' : '');
};

Date.prototype.toTimeString = function () {
    return mocent.util.padLeft(this.getHours(), 2, '0') + ":" + mocent.util.padLeft(this.getMinutes(), 2, '0') + ":" + mocent.util.padLeft(this.getSeconds(), 2, '0');
};

Date.prototype.toDateTimeString = function () {
    var month = this.getMonth() + 1;
    var day = this.getDate();
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    return mocent.util.padLeft(this.getFullYear(), 4, '0') + '-' + month + '-' + day + " " + mocent.util.padLeft(this.getHours(), 2, '0') + ":" + mocent.util.padLeft(this.getMinutes(), 2, '0') + ":" + mocent.util.padLeft(this.getSeconds(), 2, '0');
};

//初始化=======================================
mocent.getBaseUrl();
mocent.queryString();

if(mocent.appType != 2)
{
	mocent.require("config");
	mocent.isDebug = mocent["IS_DEBUG"];
    jQuery.each(mocent["DEFAULT_JS_LOAD"], function () { mocent.require(this.toString()); });
}

if (mocent.appType == 0) {
    try { 
    	mocent.require("#application"); 
    } catch (e) { 
    	alert("system can't load application.js!" + e); 
    }
}
