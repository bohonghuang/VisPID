package org.coco24.vispid

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxScreen
import ktx.scene2d.*
import ktx.box2d.*

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTextField
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.scene2d.vis.*

class FirstScreen : KtxScreen {
    fun Vector2.show04f(): String = String.format("(%.04f, %.04f)", x, y) //将二维向量转换为字符串（保留四位小树）

    val world = createWorld() //世界
    val worldWidth = Gdx.graphics.width.toFloat() //世界宽度
    val worldHeight = Gdx.graphics.height.toFloat() //世界高度
    val viewport = FitViewport(worldWidth, worldHeight) //视口
    val worldStage = Stage(viewport) //世界舞台
    val uiStage = Stage(viewport) //UI舞台

    val worldScale = 8f //世界的缩放倍数（Box2D有最大速度的限制）
    val scaledWorldWidth = worldWidth / worldScale //缩放后的世界宽度
    val scaledWorldHeight = worldHeight / worldScale //缩放后的世界高度

    val ballBody = world.body(BodyDef.BodyType.DynamicBody) {
        position.set(scaledWorldWidth / 2f, scaledWorldHeight / 2f) //设置物体的初始位置
    }.apply {
        massData.mass = 0.001f //设置物体的质量
    }
    val ballImage = Image(Texture(Gdx.files.internal("ball.png"))) //物体的图像
    val windForce = Vector2(10f, 0f) //风力

    var paused = true //是否暂停

    //延迟初始化UI控件，以便在render()函数里进行刷新
    lateinit var posXField: VisTextField
    lateinit var posYField: VisTextField
    lateinit var vecXField: VisTextField
    lateinit var vecYField: VisTextField
    lateinit var pidForceLabel: VisLabel
    lateinit var poutLabel: VisLabel
    lateinit var ioutLabel: VisLabel
    lateinit var doutLabel: VisLabel


    val cellWidth = 80f

    override fun show() {
        Gdx.input.inputProcessor = uiStage
        worldStage.addActor(ballImage)
        scene2d.visWindow(title = "环境设置") {
            visTable {
                visLabel("风力X：")
                val xValueLabel = visLabel(text = windForce.x.toString()).cell(minWidth = 100f)
                visSlider(min = 0f, max = 20f, step = 0.01f) {
                    onChange {
                        xValueLabel.setText(value.toString())
                        windForce.x = value
                    }
                }.value = windForce.x
                row()

                visLabel("风力Y：")
                val yValueLabel = visLabel(text = windForce.y.toString()).cell(minWidth = 100f)
                visSlider(min = 0f, max = 20f, step = 0.01f) {
                    onChange {
                        yValueLabel.setText(value.toString())
                        windForce.y = value
                    }
                }.value = windForce.y
            }
            row()

            visTextButton("播放").cell(grow = true).onClick {
                paused = !paused
                setText(if(paused) "播放" else "暂停")
            }
            row()

            pack()
            uiStage.addActor(this)
            setPosition(0f, stage.height, Align.topLeft)
        }
        scene2d.visWindow("物体") {
            visLabel("位置X：")
            val posX = visTextField("0").cell(width = cellWidth)
            row()
            visLabel("位置Y：")
            val posY = visTextField("0").cell(width = cellWidth)
            row()
            visLabel("速度X：")
            val vecX = visTextField("0").cell(width = cellWidth)
            row()
            visLabel("速度Y：")
            val vecY = visTextField("0").cell(width = cellWidth)
            row()

            visTextButton("应用").cell(grow = true, colspan = 2).onClick {
                ballBody.position.set(posX.text.toFloatOrNull()?:0f, posY.text.toFloatOrNull()?:0f) //直接设置物体的位置
                ballBody.linearVelocity.set(vecX.text.toFloatOrNull()?:0f, vecY.text.toFloatOrNull()?:0f) //直接设置物体的线速度
            }

            posXField = posX
            posYField = posY
            vecXField = vecX
            vecYField = vecY

            pack()
            uiStage.addActor(this)
            setPosition(stage.width, 0f, Align.bottomRight)
        }
        scene2d.visWindow("PID") {
            visTable {
                visCheckBox(text = "PID开关") {
                    isChecked = true
                    onChange {
                        pidEnabled = isChecked
                    }
                }
                row()

                visLabel("设定值X：")
                val xValueLabel = visLabel(text = target.x.toString()).cell(minWidth = 100f)
                visSlider(min = 0f, max = scaledWorldWidth, step = 1f) {
                    onChange {
                        xValueLabel.setText(value.toString())
                        target.x = value
                    }
                }.value = target.x
                row()

                visLabel("设定值Y：")
                val yValueLabel = visLabel(text = target.y.toString()).cell(minWidth = 100f)
                visSlider(min = 0f, max = scaledWorldHeight, step = 1f) {
                    onChange {
                        yValueLabel.setText(value.toString())
                        target.y = value
                    }
                }.value = target.y
                row()

                visLabel("PID参数：")
                row()

                visLabel("Kp：")
                val kpValueLabel = visLabel(text = kp.toString()).cell(minWidth = 100f)
                visSlider(min = 0f, max = 5f, step = 0.01f) {
                    onChange {
                        kpValueLabel.setText(value.toString())
                        kp = value
                    }
                }.value = kp
                row()

                visLabel("T：")
                val tValueLabel = visLabel(text = t.toString()).cell(minWidth = 100f)
                visSlider(min = 0f, max = 5f, step = 0.01f) {
                    onChange {
                        tValueLabel.setText(value.toString())
                        t = value
                    }
                }.value = t
                row()
                
                visLabel("Ti：")
                val tiValueLabel = visLabel(text = ti.toString()).cell(minWidth = 100f)
                visSlider(min = 0f, max = 5f, step = 0.001f) {
                    onChange {
                        tiValueLabel.setText(value.toString())
                        ti = value
                    }
                }.value = ti
                row()
                
                visLabel("Td：")
                val tdValueLabel = visLabel(text = td.toString()).cell(minWidth = 100f)
                visSlider(min = 0f, max = 5f, step = 0.01f) {
                    onChange {
                        tdValueLabel.setText(value.toString())
                        td = value
                    }
                }.value = td
                row()

                visLabel("Pout：")
                poutLabel = visLabel(pout.show04f()).cell(colspan = 2)
                row()
                
                visLabel("Iout：")
                ioutLabel = visLabel(iout.show04f()).cell(colspan = 2)
                row()
                
                visLabel("Dout：")
                doutLabel = visLabel(dout.show04f()).cell(colspan = 2)
                row()

                visLabel("Fout：")
                pidForceLabel = visLabel(pidForce.show04f()).cell(colspan = 2)
                row()
            }

            pack()
            uiStage.addActor(this)
            setPosition(0f, 0f, Align.bottomLeft)
        }
    }

    override fun render(delta: Float) {
        if(!paused) {
            ballBody.applyForceToCenter(windForce, true) //对物体施加风力
            pid(delta) //计算PID输出的力
            ballBody.applyForceToCenter(pidForce, true) //对物体施加PID输出的力
            world.step(delta, 8, 8) //更新物体的位置

            //刷新UI
            ballBody.apply {
                vecXField.text = linearVelocity.x.toString()
                vecYField.text = linearVelocity.y.toString()
                posXField.text = position.x.toString()
                posYField.text = position.y.toString()
            }
            pidForceLabel.setText(pidForce.show04f())
            poutLabel.setText(pout.show04f())
            ioutLabel.setText(iout.show04f())
            doutLabel.setText(dout.show04f())
        }

        //通过物体在世界的位置，设置物体在屏幕中的位置
        ballImage.x = ballBody.position.x * worldScale
        ballImage.y = ballBody.position.y * worldScale

        //绘制
        worldStage.act(delta)
        worldStage.draw()

        uiStage.act(delta)
        uiStage.draw()

        super.render(delta)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height)
    }

    val target = Vector2(ballBody.position) //PID的设定值

    val accumulation = Vector2() //历史积累的偏差值
    val last = Vector2() //前一次采样的偏差值
    val current = Vector2() //当前的偏差值

    val out = Vector2() //P、I、D输出值的线性叠加
    val pout = Vector2() //P的输出值
    val iout = Vector2() //I的输出值
    val dout = Vector2() //D的输出值

    val pidForce = Vector2() //输出的力

    var kp = 0.75f //放缩倍数
    var ti = 1f //积分常数
    var td = 1.75f //微分时间常数
    var t = 0.2f //采样周期
    var timer = 0f //采样时间的计时器
    
    var pidEnabled = true //PID是否开启

    fun pid(delta: Float) {
        if(!pidEnabled) return

        timer += delta
        if(timer < t) return
        timer -= t

        last.set(current) //E_{k-1}
        current.set(target).sub(ballBody.position) //E_k
        accumulation.add(current) //S_k

        out.setZero() //置零
        out.add(pout.set(current).scl(kp)) //比例调节
        out.add(iout.set(accumulation).scl(kp * t / ti)) //积分调节
        out.add(dout.set(current).sub(last).scl(kp * td / t)) //微分调节

        pidForce.set(out)
    }
}