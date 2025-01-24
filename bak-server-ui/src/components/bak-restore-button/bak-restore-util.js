import Vue from 'vue'
// 内置的几个cron表达式
export const buildInCronList = [
  {label: "每天一次（0 0 0 * * ?）", value: "0 0 0 * * ?"},
  {label: "每周一次（0 0 ? * 2）", value: "0 0 0 ? * 2"},
  {label: "每月一次（0 0 0 1 * ?）", value: "0 0 0 1 * ?"},
  {label: "每季度一次（0 0 0 1 1 4,7,10 ?）", value: "0 0 0 1 1,4,7,10 ?"},
  {label: "每年一次（0 0 0 1 1 ?）", value: "0 0 0 1 1 ?"},
]

export const downloadFile = function (filePath) {
    const $http = Vue.prototype.$http
    $http({
      url: $http.adornUrl(`/back-up/backLog/fileDownLoad`),
      method: "get",
      params: $http.adornParams({filePath}),
      responseType: "blob" // 确保返回的是二进制流
    }).then(res => {
      // 将可能的反斜杠(win)替换为正斜杠
      const normalizedPath = filePath.replace(/\\/g, '/');
      let fileName = normalizedPath.split('/').pop();
      if (res.headers["content-disposition"]) {
        let txt = decodeURIComponent(res.headers["content-disposition"]);
        const match = txt.match(/filename=([^;]+)/);
        if (match && match[1]) {
          fileName = match[1].trim();
        }
      }
      let blob = res.data;
      let a = document.createElement("a");
      let url = window.URL.createObjectURL(new Blob([blob], {type: 'application/octet-stream'}));
      a.href = url;
      a.download = decodeURIComponent(fileName);
      a.click();
      window.URL.revokeObjectURL(url);
    })
}
