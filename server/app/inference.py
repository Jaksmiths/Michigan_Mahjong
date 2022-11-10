import torch
from PIL import Image

def get_tiles(image_name):
    # Model
    model = torch.hub.load('ultralytics/yolov5', 'custom', path='/home/ubuntu/Michigan_Mahjong/server/app/best.pt', _verbose=False)

    # Images
    im = Image.open(image_name)  # PIL image

    # Inference
    results = model(im)

    # Results
    # results.print()
    # results.save()  # or .show()

    results_dict = results.pandas().xyxy[0].to_dict()
    if results_dict.get('name') is None:
        return []
    tile_list = [tile for tile in results_dict['name'].values()]
    return tile_list

if __name__ == "__main__":
    get_tiles('tiles.png')
