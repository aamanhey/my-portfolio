// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
 
package com.google.sps;
 
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.ArrayList.*;
import java.util.Arrays;
import java.util.Set;
 
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    //create an all day timerange
    ArrayList<TimeRange> freeSlots = new ArrayList<TimeRange>();

    if(request.getDuration() >= TimeRange.WHOLE_DAY.duration()){
        //request is longer than a day
        return freeSlots;
    }
 
    freeSlots.add(TimeRange.WHOLE_DAY);
 
    Collection<String> mandatoryAttendees = request.getAttendees();
    ArrayList<Event> mandatoryEvents = new ArrayList<Event>();
    ArrayList<Event> optionalEvents = new ArrayList<Event>();
    //organize all the mandatory and optional events
    for(Event event: events){
        if(checkForOverlap(mandatoryAttendees,event.getAttendees())){
            mandatoryEvents.add(event);
        }else{
            optionalEvents.add(event);
        }
    }
 
    //remove all the times of the mandatory events from the total timerange
    for(Event event: mandatoryEvents){
        freeSlots = removeConflict(freeSlots,event);
        freeSlots = cleanFreeSlots(freeSlots,request.getDuration());
    }
    
    return freeSlots;
  }
 
  private static void printStringTimeRange(ArrayList<TimeRange> slots, String name){
    String slotsString = "The free slots for "+name+": ";
    for(TimeRange slot: slots){
        slotsString += slot + ", ";
    }
    System.out.println(slotsString);
  }
 
  private static void printStringEvent(ArrayList<Event> slots, String name){
    String slotsString = "The events for "+name+": ";
    for(Event slot: slots){
        slotsString += "[" + slot.getWhen().start() + " to " + slot.getWhen().end() + "], ";
    }
    slotsString += " with a length of "+ slots.size();
    System.out.println(slotsString);
  }
 
  private static void printTimeRange(TimeRange time, String name){
      String asString = "Timerange called " + name + " from " + time.start() + " to " + time.end() + ".";
      System.out.println(asString);
  }
 
  private boolean checkForOverlap(Collection<String> mandatory, Set<String> attendees){
      //compares to see if there is overlap in the attendees of a collection and set
      for(String person:attendees){
          if(mandatory.contains(person)){
              return true;
          }
      }
 
      return false;
  }
 
  private ArrayList<TimeRange> removeConflict(ArrayList<TimeRange> freeSlots, Event event){
    TimeRange takenSlot = event.getWhen();
    int takenSlotStart = takenSlot.start();
    int takenSlotEnd = takenSlot.end();
 
    ArrayList<TimeRange> timeRangeHolderList = new ArrayList<TimeRange>();
    ArrayList<Integer> indexHolderList = new ArrayList<Integer>();
 
    for(TimeRange slot: freeSlots){
        if(slot.start() <= takenSlotStart && slot.end() >= takenSlotEnd){
          //  |------free--------|
          //     |--event--|
          int slotIndex = freeSlots.indexOf(slot);
          TimeRange priorSlotTime = TimeRange.fromStartEnd(slot.start(),takenSlotStart,false);
          TimeRange postSlotTime = TimeRange.fromStartEnd(takenSlotEnd,slot.end(),false);
 
          indexHolderList.add(slotIndex);
          timeRangeHolderList.add(postSlotTime);
          timeRangeHolderList.add(priorSlotTime);
        }else if(slot.start() < takenSlotStart && slot.end() > takenSlotStart){
          // |-----free----|
          //      |----event---|
          int slotIndex = freeSlots.indexOf(slot);
          TimeRange priorSlotTime = TimeRange.fromStartEnd(slot.start(),takenSlotStart,false);
 
          indexHolderList.add(slotIndex);
          timeRangeHolderList.add(null);//holder in the array
          timeRangeHolderList.add(priorSlotTime);
        }else if(slot.start() < takenSlotEnd && slot.end() > takenSlotEnd){
          //      |----free-----|
          // |---event----|
          int slotIndex = freeSlots.indexOf(slot);
          TimeRange postSlotTime = TimeRange.fromStartEnd(takenSlotEnd,slot.end(),false);
 
          indexHolderList.add(slotIndex);
          timeRangeHolderList.add(null);//holder in the array
          timeRangeHolderList.add(postSlotTime);
        }else{
          // no overlap
          // |--free--| |--event--|
          // free slot not big enough
          //     |--free--|
          // |-----event------|
        }
    }
    
    int i;
    int j = 0;
    int holderLength = timeRangeHolderList.size();
    for(i=0;i<holderLength;i+=2){
        int slotIndex = indexHolderList.get(i);
        freeSlots.remove(slotIndex);

        if(holderLength > i && timeRangeHolderList.get(i) != null){
        TimeRange postSlot = timeRangeHolderList.get(i);
        freeSlots.add(slotIndex,postSlot);
        }

        if(holderLength > i+1 && timeRangeHolderList.get(i+1) != null){
            TimeRange priorSlot = timeRangeHolderList.get(i+1);
            freeSlots.add(slotIndex,priorSlot);
        }
        j++;
    }
    return freeSlots;
  }
 
  private ArrayList<TimeRange> cleanFreeSlots(ArrayList<TimeRange> freeSlots, long duration){
      //goes through the freeSlots and removes any timeranges less than the duration of the meeting
      ArrayList<Integer> indexHolderList = new ArrayList<Integer>();
      for(TimeRange slot:freeSlots){
          if(slot.duration() < duration){
              indexHolderList.add(0,freeSlots.indexOf(slot));
          }
      }
      for(int index: indexHolderList){
        freeSlots.remove(index);
      }
 
      return freeSlots;
  }
}

