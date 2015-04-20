package lab.s2jh.module.ad.web;

import lab.s2jh.core.annotation.MenuData;
import lab.s2jh.core.service.BaseService;
import lab.s2jh.core.service.Validation;
import lab.s2jh.core.web.BaseController;
import lab.s2jh.core.web.view.OperationResult;
import lab.s2jh.module.ad.entity.Advertising;
import lab.s2jh.module.ad.service.AdvertisingService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/admin/ad/advertising")
public class AdvertisingController extends BaseController<Advertising, Long> {
    @Autowired
    private AdvertisingService advertisingService;

    @Override
    protected BaseService<Advertising, Long> getEntityService() {
        return advertisingService;
    }

    @Override
    protected Advertising buildBindingEntity() {
        return new Advertising();
    }


    @MenuData("配置管理:广告流量:广告配置")
    @RequiresPermissions("配置管理:广告流量:广告配置")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        return "admin/ad/advertising-index";
    }

    @RequiresPermissions("配置管理:广告流量:广告配置")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Page<Advertising> findByPage(HttpServletRequest request) {
        return super.findByPage(Advertising.class, request);
    }

    @RequiresPermissions("配置管理:广告流量:广告配置")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult editSave(@ModelAttribute("entity") Advertising entity, Model model) {
        return super.editSave(entity);
    }
    @RequiresPermissions("配置管理:广告流量:广告配置")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult delete(@RequestParam("ids") Long... ids) {
        return super.delete(ids);
    }

    @RequiresPermissions("配置管理:广告流量:广告配置")
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editShow() {
        return "admin/ad/advertising-inputBasic";
    }
    @RequiresUser
    @ModelAttribute
    public void prepareModel(Model model, @RequestParam(value = "id", required = false) Long id) {
        super.initPrepareModel(model, id);
    }
}
