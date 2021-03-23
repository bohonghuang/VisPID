package org.coco24.vispid

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.color.ColorPickerStyle
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.rpsg.lazyFont.LazyBitmapFont
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.style.*
class PidVisualization : KtxGame<KtxScreen>() {
    override fun create() {
        Box2D.init() //初始化Box2D
        initVisUI() //初始化VisUI
        currentScreen = FirstScreen()
        super.create()
    }
    fun initVisUI() {
        val scale = Gdx.graphics.density

        if(scale >= 1f) VisUI.load(VisUI.SkinScale.X2)
        else VisUI.load(VisUI.SkinScale.X1)

        LazyBitmapFont.setGlobalGenerator(FreeTypeFontGenerator(Gdx.files.internal("msyh.ttc")))
        val defaultFont = LazyBitmapFont((22 * scale).toInt(), Color.WHITE)
        
        VisUI.getSkin().get(Label.LabelStyle::class.java).font = defaultFont
        VisUI.getSkin().get(VisTextButton.VisTextButtonStyle::class.java).font = defaultFont
        VisUI.getSkin().get(VisTextField.VisTextFieldStyle::class.java).font = defaultFont
        VisUI.getSkin().get(MenuItem.MenuItemStyle::class.java).font = defaultFont
        VisUI.getSkin().get(Menu.MenuStyle::class.java).openButtonStyle.font = defaultFont
        VisUI.getSkin().get(List.ListStyle::class.java).font = defaultFont
        VisUI.getSkin().get(ColorPickerStyle::class.java).titleFont = defaultFont
        VisUI.getSkin().get(Window.WindowStyle::class.java).titleFont = defaultFont
        VisUI.getSkin().get("radio", VisCheckBox.VisCheckBoxStyle::class.java).font = defaultFont
        VisUI.getSkin().get(SelectBox.SelectBoxStyle::class.java).font = defaultFont
        VisUI.getSkin().get(SelectBox.SelectBoxStyle::class.java).listStyle.font = defaultFont
        VisUI.getSkin().get(VisCheckBox.VisCheckBoxStyle::class.java).font = defaultFont
        VisUI.getSkin().get(LinkLabel.LinkLabelStyle::class.java).font = defaultFont
        VisUI.getSkin().get(TabbedPane.TabbedPaneStyle::class.java).buttonStyle.font = defaultFont
    }
}