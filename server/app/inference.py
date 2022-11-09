import torch
from PIL import Image

# Model
model = torch.hub.load('ultralytics/yolov5', 'custom', path='best.pt')

# Images
im = Image.open('tiles.png')  # PIL image

# Inference
results = model(im)

# Results
results.print()
results.save()  # or .show()

results.xyxy[0]  # im1 predictions (tensor)
results.pandas().xyxy[0]  # im1 predictions (pandas)
