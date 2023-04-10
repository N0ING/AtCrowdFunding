package com.no.crowd.service.impl;

import com.no.crowd.entity.Menu;
import com.no.crowd.entity.MenuExample;
import com.no.crowd.mapper.MenuMapper;
import com.no.crowd.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<Menu> getAll() {

        MenuExample menuExample = new MenuExample();

        List<Menu> menuList = menuMapper.selectByExample(menuExample);

        return menuList;
    }

    @Override
    public void saveMenu(Menu menu) {
        menuMapper.insert(menu);
    }

    @Override
    public void updateMenu(Menu menu) {
        menuMapper.updateByPrimaryKeySelective(menu);
    }

    @Override
    public void removeMenu(Integer id) {
        menuMapper.deleteByPrimaryKey(id);
    }
}
