import { request } from '../request';

/** get constant routes */
export async function fetchGetConstantRoutes() {
  return {
    error: null,
    data:[
      {
        name: 'login',
        path: '/login/:module(pwd-login|code-login|register|reset-pwd|bind-wechat)?',
        component: 'layout.blank$view.login',
        meta: {
          title: '登录',
          i18nKey: 'route.login',
          constant: true,
          hideInMenu: true
        }
      },
      {
        name: '403',
        path: '/403',
        component: 'layout.blank$view.403',
        meta: {
          title: '无权限',
          i18nKey: 'route.403',
          constant: true,
          hideInMenu: true
        }
      },
      {
        name: '404',
        path: '/404',
        component: 'layout.blank$view.404',
        meta: {
          title: '未找到',
          i18nKey: 'route.404',
          constant: true,
          hideInMenu: true
        }
      },
      {
        name: '500',
        path: '/500',
        component: 'layout.blank$view.500',
        meta: {
          title: '服务器错误',
          i18nKey: 'route.500',
          constant: true,
          hideInMenu: true
        }
      }
    ]
  } as any;
}

/** get user routes */
export function fetchGetUserRoutes() {
  return request<any>({
    url: '/route/getUserRoutes',
    method: 'get'
  });
}

/**
 * whether the route is exist
 *
 * @param routeName route name
 */
export function fetchIsRouteExist(routeName: string) {
  return request<boolean>({
    url: '/route/isRouteExist',
    method: 'get',
    params: { routeName }
  });
}
