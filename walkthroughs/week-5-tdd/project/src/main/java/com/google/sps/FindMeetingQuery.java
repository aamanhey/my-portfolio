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
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    ArrayList<Event> mandatoryEvents = new ArrayList<Event>();
    ArrayList<Event> optionalEvents = new ArrayList<Event>();
    
    //organize all the mandatory and optional events
    for(Event event: events){
        if(checkForOverlap(mandatoryAttendees,event.getAttendees())){
            mandatoryEvents.add(event);
        }else if(checkForOverlap(optionalAttendees,event.getAttendees())){
            optionalEvents.add(event);
        }else{
            //not attending
        }
    }
 
    //remove all the times of the mandatory events from the total timerange
    for(Event event: mandatoryEvents){
        freeSlots = removeConflict(freeSlots,event);
        freeSlots = cleanFreeSlots(freeSlots,request.getDuration());
    }

    ArrayList<TimeRange> mandatorySlots = new ArrayList<TimeRange>();
    for(TimeRange each: freeSlots){
        mandatorySlots.add(each);
    }
    ArrayList<TimeRange> optionalFreeSlots = freeSlots;

    //remove all the times of the optional events from the total timerange
    for(Event event: optionalEvents){
        optionalFreeSlots = freeSlots;
        optionalFreeSlots = removeConflict(optionalFreeSlots,event);
        optionalFreeSlots = cleanFreeSlots(optionalFreeSlots,request.getDuration());
    }

    if(optionalFreeSlots.size() > 0){
        return optionalFreeSlots;
    }else if(mandatoryEvents.size() == 0){
        return new ArrayList<TimeRange>();
    }else{
        return mandatorySlots;
    }
  }


  private ArrayList<TimeRange> getNew(ArrayList<TimeRange> array1, ArrayList<TimeRange> array2){
    // returns the unshared timeranges

    ArrayList<TimeRange> holderList = new ArrayList<TimeRange>();
    if(array1 ==null || array2 == null || array1.size() < 1 || array2.size() < 1){
        return holderList;
    }

    holderList = array2;
    if(array1.size() > array2.size()){
        holderList = array1;
        array1 = array2;
        array2 = holderList;
    }

    for(TimeRange each: array1){
        if(array2.contains(each)){
            holderList.remove(each);
        }
    }
    return holderList;
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

