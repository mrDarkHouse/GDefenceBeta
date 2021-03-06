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
package com.darkhouse.gdefence.InventorySystem.inventory.Source;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Array;
import com.darkhouse.gdefence.GDefence;
import com.darkhouse.gdefence.InventorySystem.inventory.Slot;
import com.darkhouse.gdefence.InventorySystem.inventory.SlotActor;
import com.darkhouse.gdefence.InventorySystem.inventory.Target.SlotTarget;
import com.darkhouse.gdefence.Objects.GameObject;
import com.darkhouse.gdefence.Objects.TowerObject;

public class SlotSource extends Source {

	protected Slot sourceSlot;
	protected Slot payloadSlot;
//	protected Slot targetSlot;

	public SlotSource(SlotActor actor) {
		super(actor);
		this.sourceSlot = actor.getSlot();
	}

	@Override
	public Payload dragStart(InputEvent event, float x, float y, int pointer) {
		if (sourceSlot.getAmount() == 0) {
			return null;
		}

		Payload payload = new Payload();
//		Slot payloadSlot = new Slot(sourceSlot.getPrototype(), sourceSlot.getAmount());
		payloadSlot = new Slot(sourceSlot.getType()/*, sourceSlot.getPrototype(), 0*/);
		if(!takeSlot()) return null;
		payload.setObject(payloadSlot);

		//TextureAtlas icons = LibgdxUtils.assets.get("icons/icons.atlas", TextureAtlas.class);
		TextureAtlas icons = /*new TextureAtlas(*//*Gdx.files.internal("icons/icons.atlas")*/GDefence.getInstance().assetLoader.get("icons/icons.atlas", TextureAtlas.class)/*)*/;
		TextureRegion icon = icons.findRegion(payloadSlot.getPrototype().getTextureRegion());

		Actor dragActor = new Image(icon);
//        dragActor.setSize(35, 35);
		payload.setDragActor(dragActor);

		Actor validDragActor = new Image(icon);
//        validDragActor.setSize(35, 35);
		// validDragActor.setColor(0, 1, 0, 1);
		payload.setValidDragActor(validDragActor);

		Actor invalidDragActor = new Image(icon);
//        invalidDragActor.setSize(35, 35);
		// invalidDragActor.setColor(1, 0, 0, 1);
		payload.setInvalidDragActor(invalidDragActor);

		return payload;
	}
	protected boolean takeSlot(){
		if(Gdx.input.isButtonPressed(0)) payloadSlot.add(sourceSlot.takeAll());
		else if(Gdx.input.isButtonPressed(1)) payloadSlot.add(sourceSlot.take(1));
		else return false;

		return true;
	}

	@Override
	public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
		//Slot payloadSlot = (Slot) payload.getObject();
		if (target == null) {
			ifNullTarget();
		} else if(target instanceof SlotTarget){
			ifSlotTarget(target);
		} /*else {// other targets
			System.out.println("else");
			ifNullTarget();
		}*/
	}
	protected void ifNullTarget(){
		sourceSlot.add(payloadSlot.takeAll());
	}
	protected void ifSlotTarget(Target target){
		Slot targetSlot = ((SlotActor) target.getActor()).getSlot();
		boolean sameType = false;
		for (Class<? extends GameObject> i : targetSlot.getType()){
			if(TowerObject.isMatches(i, payloadSlot.getLast().getClass())){
				sameType = true;
			}
		}
		if(/*targetSlot.getType() != payloadSlot.getType() && targetSlot.getType().getSuperclass() != payloadSlot.getType()
				&& payloadSlot.getType().getSuperclass() != targetSlot.getType()*/!sameType) {//recipe extends detail
			ifNullTarget();
			return;
		}//new engine lol
		if (targetSlot.matches(payloadSlot) || targetSlot.isEmpty()){

//			targetSlot.add(payloadSlot.takeAll());
			int toMove = targetSlot.getMaxItems() - targetSlot.getAmount();//if < 0 error with maxItems
			if(toMove == 0) {//cant swap with low possible number slots
				ifNullTarget();
				return;
			}
            checkCount(targetSlot, toMove);
		} else{//swap slot items
			Array<? extends GameObject> tmp = targetSlot.takeAll();
			targetSlot.add(payloadSlot.takeAll());
			sourceSlot.add(tmp);
		}
	}
	protected void checkCount(Slot targetSlot, int toMove){
		if(payloadSlot.getAmount() <= toMove) {
			move(targetSlot, payloadSlot.takeAll());
//				targetSlot.add(payloadSlot.takeAll());
		} else {
			move(targetSlot, payloadSlot.take(toMove));
//				targetSlot.add(payloadSlot.take(toMove));
			sourceSlot.add(payloadSlot.takeAll());
		}
	}

	protected void move(Slot targetSlot, Array<? extends GameObject> objects){
		targetSlot.add(objects);
	}


}
