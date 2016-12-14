/* Copyright (c) 2014 PixelScientists
 * 
 * The MIT License (MIT)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.darkhouse.gdefence.InventorySystem.inventory.Tooltip;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.darkhouse.gdefence.GDefence;
import com.darkhouse.gdefence.Helpers.FontLoader;
import com.darkhouse.gdefence.InventorySystem.inventory.Slot;
import com.darkhouse.gdefence.InventorySystem.inventory.SlotListener;
import com.darkhouse.gdefence.Objects.SpellObject;
import com.darkhouse.gdefence.Objects.TowerObject;

/**
 * @author Daniel Holderbaum
 */
public class SlotTooltip extends Window implements SlotListener {

	private Skin skin;

	private Slot slot;

	public SlotTooltip(Stage stage, Slot slot, Skin skin) {
		super("Tooltip...", skin);
		this.slot = slot;
		this.skin = skin;
		hasChanged(slot);
		slot.addListener(this);
		setVisible(false);

		stage.addActor(this);
//		GDefence.getInstance().getSmith().getStage().addActor(this);
	}

	@Override
	public void hasChanged(Slot slot) {
		if (slot.isEmpty()) {
			setVisible(false);
			return;
		}

		// title displays the amount

		//setTitle(slot.getAmount() + "x " + slot.getPrototype());
		getTitleLabel().setText(/*slot.getAmount() + "x " + */slot.getLast().getName());
		clear();
		//Label label = //new Label("Super awesome description of " + slot.getPrototype(), skin);
		Label label = new Label(slot.getLast().getTooltip(), skin);
		add(label).row();//row can bad
		if (slot.getLast() instanceof TowerObject) {
			TowerObject t = ((TowerObject) slot.getLast());

            /*FontLoader.generateStyle(16, Color.WHITE)*/
            Label level = new Label(t.getLevel() + "", skin);//allow FontLoader load skin fonts
            add(level).align(Align.center).row();



			ProgressBar expBar = new ProgressBar(0, GDefence.getInstance().user.getMaxExpThisLvl(), 0.2f, false,
					GDefence.getInstance().assetLoader.getExpBarSkin());//add text inside
			expBar.getStyle().background.setMinHeight(20);
			expBar.getStyle().knob.setMinHeight(20);
			expBar.getStyle().background.setMinWidth(50);
			expBar.getStyle().knob.setMinWidth(0.1f);
			expBar.setSize(50, 20);
			expBar.setValue(t.getCurrentExp());
			add(expBar);
		}
		pack();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		// the listener sets this to true in case the slot is hovered
		// however, we don't want that in case the slot is empty
		if (slot.isEmpty()) {
			super.setVisible(false);
		}
	}

}
