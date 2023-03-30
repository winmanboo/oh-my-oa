import request from "@/utils/request";

const api_name = '/admin/system/sysRole';

export default {
  // 角色列表-条件分页查询
  getpageList(current, limit, searchObj) {
    return request({
      url: `${api_name}/${current}/${limit}`,
      method: 'get',
      params: searchObj
    })
  }
}
