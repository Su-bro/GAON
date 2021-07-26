import { createRouter, createWebHistory } from "vue-router";
import Home from "@/views/home/home";
import ConferencesDetail from "@/views/conferences/conference-detail";
import History from "@/views/history/history";
import GoogleLogin from "@/views/google-login/google-login.vue";
const fullMenu = require("@/views/main/menu.json");
function makeRoutesFromMenu() {
  let routes = Object.keys(fullMenu).map(key => {
    if (key === "home") {
      return { path: fullMenu[key].path, name: key, component: Home };
    } else if (key === "history") {
      return { path: fullMenu[key].path, name: key, component: History };
    }
    // 맨처음 시작화면 google로그인 창 띄우기
    else if (key === "google") {
      return { path: fullMenu[key].path, name: key, component: GoogleLogin };
    } else {
      // menu.json 에 들어있는 로그아웃 메뉴
      return null;
    }
  });
  // 로그아웃 파싱한 부분 제거
  routes = routes.filter(item => item);
  // menu 자체에는 나오지 않는 페이지 라우터에 추가(방 상세보기)
  routes.push({
    path: "/conferences/:conferenceId",
    name: "conference-detail",
    component: ConferencesDetail
  });
  return routes;
}

const routes = makeRoutesFromMenu();

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.afterEach(to => {
  console.log(to);
});

export default router;
